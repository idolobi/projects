package com.es.deployer.deploy.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.es.deployer.common.Const;
import com.es.deployer.common.Parameter;
import com.es.deployer.common.Property;
import com.es.deployer.deploy.dao.DeployDao;
import com.es.deployer.deploy.dao.RequestDao;
import com.es.deployer.deploy.dto.DeployDto;
import com.es.deployer.doc.dao.StatusDao;
import com.es.deployer.doc.dto.StatusDto;
import com.es.deployer.file.FileCopyEntry;
import com.es.deployer.file.FileUtil;
import com.es.deployer.ftp.FTPClientManager;

public class Deploy {
	
	private RequestDao requestDao;
	private DeployDao deployDao;
	private StatusDao  statusDao;
	private TransactionTemplate transactionTemplate;
	
	public void setRequestDao(RequestDao requestDao) {
		this.requestDao = requestDao;
	}
	
	public void setDeployDao(DeployDao deployDao) {
		this.deployDao = deployDao;
	}
	
	public void setStatusDao(StatusDao statusDao) {
		this.statusDao = statusDao;
	}
	
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
	    this.transactionTemplate = transactionTemplate;
	}
	
	/**
	 * 배포
	 * @param param
	 * @throws IOException
	 */
	public void deploy(Parameter param) throws IOException {
		Property prpt = new Property(param.getService()+"."+param.getPhase());
		
		String packPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/" 
                        + prpt.getHdsnPackPath()  + "/" + param.getDepId();
		
		List<String> files = FileUtil.getFileList(packPath);
		List<FileCopyEntry> entries = new ArrayList<FileCopyEntry>();
		int i = 0;
		while(i < files.size()) {
			String path = files.get(i).replace('\\', '/');
			FileCopyEntry entry = new FileCopyEntry();
			entry.setFromPath(path.replaceFirst(packPath, packPath+"_backup"));
			entry.setToPath(path.replaceFirst(packPath, prpt.getSvrDocRoot()));
			entries.add(entry);
			i++;
		}
		System.out.println("★ 대상 : " + i + "개");
		
		//backup(ftp download)
		int cnt = FTPClientManager.download(param.getSvrInfo(), entries, false);
		
		System.out.println("★ 백업 : " + cnt + "개");
	}
	
	/**
	 * 접수 상태를 시험배포 상태로 상태 전환(정기, 긴급, 테스트)
	 * @param param
	 */
	public void recordTestDeploy(Parameter param) {
		//int rtn = 0;
		Property prpt = new Property(param.getService()+"."+param.getPhase());
		
		final String projId = prpt.getProjId();
		final String srcGrupCd = prpt.getSrcGrupCd();
		final String depId = param.getDepId();
		final String phase = param.getPhase();
		final String usrId = Const.USR_DEPLOYER;
		
		String[] tempRglrUrgntDivCds = null;
		String[] tempDepReqIds = null;
		if((Const.L2_PROJ_ID.equals(projId) || Const.L3_PROJ_ID.equals(projId)) 
				&& param.getRglrUrgnt() != null) {
			tempRglrUrgntDivCds = param.getRglrUrgnt().length() >= 1 ? param.getRglrUrgnt().split(",") : null;
			tempDepReqIds = param.getDepReqId().length() >= 10 ? param.getDepReqId().split(",") : null;
		}
		
		final String[] rglrUrgntDivCds = tempRglrUrgntDivCds;
		final String[] depReqIds = tempDepReqIds;
		
		transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				// projId와 depId로 배포정보 획득
				DeployDto deployDto = deployDao.selectDeployInfo(projId, depId);
				String depReqDate = deployDto.getDepDate();				
				String rglrUrgntDivCd = deployDto.getRglrUrgntDivCd();
				String docId = Const.DIV_TEST.equals(rglrUrgntDivCd) ? Const.DOC_REQUEST_TEST : Const.DOC_REQUEST; 
				String phsCd = statusDao.getPhsCdOnEvtNm(projId, docId, Const.EVT_NM_RECEIPT);
				// lecs2를 위한 hardcoding, 임시
				if((Const.L2_PROJ_ID.equals(projId) || Const.L3_PROJ_ID.equals(projId))
						&& Const.PHASE_TEST.equals(phase)) {
					phsCd = statusDao.getPhsCdOnEvtNm();
				}
				String fwdPhsCd = statusDao.getFwdPhsCdOnPhsCd(projId, docId, phsCd);
				
				StatusDto requestStatusDto = new StatusDto();
				requestStatusDto.setProjId(projId);
				requestStatusDto.setSrcGrupCd(srcGrupCd);
				requestStatusDto.setDepReqDate(depReqDate);
				if(Const.L2_PROJ_ID.equals(projId) || Const.L3_PROJ_ID.equals(projId)) {
					requestStatusDto.setRglrUrgntDivCds(rglrUrgntDivCds);
					requestStatusDto.setDepReqIds(depReqIds);
				} else {
					requestStatusDto.setRglrUrgntDivCd(rglrUrgntDivCd);
				}
				requestStatusDto.setPhsCd(phsCd); // phsCd - 접수
				requestStatusDto.setFwardPhsCd(fwdPhsCd); // fwardPhsCd - 시험배포
				requestStatusDto.setDepId(depId);
				requestStatusDto.setDocId(docId);
				requestStatusDto.setComments("자동배포");
				requestStatusDto.setFirstInptUsrId(usrId);
				requestStatusDto.setLastModiUsrId(usrId);

				requestDao.updatePhsProcMaxSeq(requestStatusDto);
				requestDao.addReqDocStat(requestStatusDto);				
				
				return 0;
			}
		});
		//return rtn;
	}
	
	/**
	 * 테스트성공 상태를 다음 상태로 변경
	 * @param param
	 */
	public void recordRealDeploy(Parameter param) {
		Property prpt = new Property(param.getService()+"."+param.getPhase());
		
		final String projId = prpt.getProjId();
		final String srcGrupCd = prpt.getSrcGrupCd();
		final String depId = param.getDepId();
		final String usrId = Const.USR_DEPLOYER;
		
		String[] tempRglrUrgntDivCds = null;
		String[] tempDepReqIds = null;
		if((Const.L2_PROJ_ID.equals(projId) || Const.L3_PROJ_ID.equals(projId)) 
				&& param.getRglrUrgnt() != null) {
			tempRglrUrgntDivCds = param.getRglrUrgnt().length() >= 1 ? param.getRglrUrgnt().split(",") : null;
			tempDepReqIds = param.getDepReqId().length() >= 10 ? param.getDepReqId().split(",") : null;
		}
		
		final String[] rglrUrgntDivCds = tempRglrUrgntDivCds;
		final String[] depReqIds = tempDepReqIds;
		
		transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				// projId와 depId로 배포정보 획득
				DeployDto deployDto = deployDao.selectDeployInfo(projId, depId);
				String depReqDate = deployDto.getDepDate();
				String rglrUrgntDivCd = deployDto.getRglrUrgntDivCd();
				String docId = Const.DOC_REQUEST;
				String phsCd = statusDao.getPhsCdOnEvtNm(projId, docId, Const.EVT_NM_PASS);
				String fwdPhsCd = statusDao.getFwdPhsCdOnPhsCd(projId, docId, phsCd);
				
				StatusDto requestStatusDto = new StatusDto();
				requestStatusDto.setProjId(projId);
				requestStatusDto.setSrcGrupCd(srcGrupCd);
				requestStatusDto.setDepReqDate(depReqDate);
				if(Const.L2_PROJ_ID.equals(projId) || Const.L3_PROJ_ID.equals(projId)) {
					requestStatusDto.setRglrUrgntDivCds(rglrUrgntDivCds);
					requestStatusDto.setDepReqIds(depReqIds);
				} else {
					requestStatusDto.setRglrUrgntDivCd(rglrUrgntDivCd);
				}
				requestStatusDto.setPhsCd(phsCd); // phsCd - 테스트성공
				requestStatusDto.setFwardPhsCd(fwdPhsCd); // fwardPhsCd - 운영배포
				requestStatusDto.setDepId(depId);
				requestStatusDto.setDocId(docId);
				requestStatusDto.setFirstInptUsrId(usrId);
				requestStatusDto.setLastModiUsrId(usrId);
				
				requestDao.updatePhsProcMaxSeq(requestStatusDto);
				requestDao.addReqDocStat(requestStatusDto);
				
				return 0;
			}
		});
	}	
}
