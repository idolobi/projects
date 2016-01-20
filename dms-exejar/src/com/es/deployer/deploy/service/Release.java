package com.es.deployer.deploy.service;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.es.deployer.common.Const;
import com.es.deployer.common.DeployerException;
import com.es.deployer.common.Parameter;
import com.es.deployer.common.Property;
import com.es.deployer.deploy.dao.DeployDao;
import com.es.deployer.deploy.dao.RequestDao;
import com.es.deployer.doc.dao.StatusDao;
import com.es.deployer.doc.dto.StatusDto;

public class Release {
	private static Logger logger = Logger.getLogger(Release.class);
	
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
	 * 자동 잠금 해제 처리
	 * @deprecated
	 * @param param
	 * @return
	 * @throws DeployerException
	 */
	public int releaseRegularRequest(Parameter param) throws DeployerException {
		final String phase = param.getPhase();
		Property prpt = new Property(param.getService()+"."+param.getPhase());
		
		final String projId = prpt.getProjId();
		final String srcGrupCd = prpt.getSrcGrupCd();
		final String depId = param.getDepId();
		logger.debug("# depId : " + depId);
		final String depReqDate = splitDepDate(depId);
		final String rglrUrgntDivCd = Const.DIV_REGULAR;
		final String usrId = Const.USR_DEPLOYER;
		final String docId = Const.DOC_REQUEST;
		
		if(!depId.equals(deployDao.makeRglrDepId(projId, srcGrupCd))) {
			throw new DeployerException("★ 배포ID가 일치하지 않습니다.");
		}
		
		int rtn = transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				
				String phsCd = statusDao.getPhsCdOnEvtNm(projId, docId, Const.EVT_NM_RECEIPT);
				String fwdPhsCd = statusDao.getFwdPhsCdOnPhsCd(projId, docId, phsCd);
				
				StatusDto requestStatusDto = new StatusDto();
				requestStatusDto.setProjId(projId);
				requestStatusDto.setSrcGrupCd(srcGrupCd);
				requestStatusDto.setDepReqDate(depReqDate);
				requestStatusDto.setRglrUrgntDivCd(rglrUrgntDivCd);
				requestStatusDto.setPhsCd(phsCd); // phsCd - 접수
				requestStatusDto.setFwardPhsCd(fwdPhsCd); // fwardPhsCd - 시험배포
				requestStatusDto.setDepId(depId);
				requestStatusDto.setDocId(docId);
				requestStatusDto.setFirstInptUsrId(usrId);
				requestStatusDto.setLastModiUsrId(usrId);
				
				requestDao.updatePhsProcMaxSeq(requestStatusDto);
				requestDao.addReqDocStat(requestStatusDto);				
				
				if(deployDao.existDepId(projId, depId)) {
					if(deployDao.isLocked(projId, depId, phase)) {
						deployDao.unlock(projId, depId, phase);
						System.out.println("★ 프로젝트("+projId+")의 배포ID("+depId+")에 대한 잠금을 해제했습니다.");
					} else {
						System.out.println("★ 프로젝트("+projId+")의 배포ID("+depId+")는 이미 잠금이 해제된 상태입니다.");
					}
				}
				
				return 1;				
			}
		});
		return rtn;
	}
	
	/**
	 * 
	 * @param depId
	 * @return
	 */
	private String splitDepDate(String depId) {
		String[] str = depId.split("_");
		return str[1];
	}
	
}
