package com.es.deployer.deploy.service;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.es.deployer.common.Parameter;
import com.es.deployer.common.Property;
import com.es.deployer.deploy.dao.DeployLogDao;
import com.es.deployer.deploy.dao.RequestDao;
import com.es.deployer.deploy.dto.DeployLogDto;
import com.es.deployer.deploy.dto.SourceDto;
import com.es.deployer.svn.SvnClientManager;
import com.es.deployer.svn.SvnEntry;

public class Merge {
	private static Logger logger = Logger.getLogger(Merge.class);
	
	private RequestDao requestDao;
	private DeployLogDao deployLogDao;
	
	public void setRequestDao(RequestDao requestDao) {
		this.requestDao = requestDao;
	}
	public void setDeployLogDao(DeployLogDao deployLogDao) {
		this.deployLogDao = deployLogDao;
	}

	public void merge(Parameter param) throws Exception	{
		Property prpt = new Property(param.getService()+"."+param.getPhase());
		
		String baseTgtPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/" + prpt.getHdsnBranchPath();
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", prpt.getProjId());
		paramMap.put("depId", param.getDepId());
		paramMap.put("depPhsDivCd", prpt.getDepPhsDivCd());
		paramMap.put("phsCd", prpt.getPhsCd());
		
		boolean isDone = false;
		int deployCnt = deployLogDao.getDeployTimes(paramMap); // 배포횟수
		System.out.println("★ 배포ID " + param.getDepId() + "의  " + (deployCnt+1) + "번째 빌드를 시작합니다.....");
				
		List<SourceDto> depSrcList = requestDao.getSvnSource(paramMap); // DB에서 조회한 소스목록
				
		int depSrcCnt = depSrcList.size();
		System.out.println("★ " + depSrcCnt + "건의 유효한 소스가 조회되었습니다.");
		
		/*
			[SvnSourceDto]		[SvnEntry]			[WorkingCopy]			[]
			projId									X						param.getProjId()
			srcGrupCd								X						prpt.getSrcGrupCd()
			depReqId								X
			seq										X
	    	svnSrcUrl			svnSrcUrl			X						
	    	svnSrcNm			svnFilename			X			
	    	svnSrcRevNum		svnRevision			svnWcRevision			
			svnSrcAthrId		svnAuthor			X						
			svnModiTime			svnDate				X						
			svnReposCd								X
								svnPath				X						svnSrcUrl.replaceFirst(prpt.getSvnSvrUrl() + "/" + prpt.getSvnReposNm() + "/" + prpt.getHdsnTrunkPath + "/", "")
								svnComment			X
													wcHeadPath				baseSrcPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/" + prpt.getHdsnTrunkPath()
													wcTailPath				svnPath + "/" + svnFilename
		 */
		
		SvnClientManager scm = new SvnClientManager(
				prpt.getSvnSvrUrl() + "/" + prpt.getSvnReposNm(), 
				prpt.getSvnUserId(), prpt.getSvnUserPassword());
		
		List<SvnEntry> depEntries = scm.listOnServer(depSrcList); // SVN(trunk)에서 조회한 소스목록
		int depEntryCnt = depEntries.size();
		if(depSrcCnt == depEntryCnt) { // depSrcCnt == depEntryCnt이면, 검증이 완료된 것으로 판단
			System.out.println("★ 새로 추가된 소스를 찾습니다.....");
			logger.debug("########## Begin search new sources.");
			List<SourceDto> addSrcList = requestDao.getAddedSource(paramMap);
			logger.debug("########## End   search new sources.");
			if(addSrcList.size() > 0) {
				System.out.println("★ " + addSrcList.size() + "개의  새로 추가된 소스를 찾았습니다.");
				List<SourceDto> rbSrcList = scm.getRollbackInfo(addSrcList, prpt); // 작업 전 롤백을 위한 정보 수집
				for(int i = 0; i < rbSrcList.size(); i++) {
					logger.debug(rbSrcList.get(i).getSvnSrcUrl() + "/" 
							+ rbSrcList.get(i).getSvnSrcNm() + " " 
							+ rbSrcList.get(i).getSvnSrcRevNum());
				}
				int rbCnt = deployLogDao.addRollbackSourceLog(rbSrcList, paramMap); // 롤백을 위한 정보를 데이터베이스에 저장(DP006)
				System.out.println("★ " + rbCnt + "개의  롤백정보를 저장했습니다.");
			} else {
				System.out.println("★ 새로 추가된 소스가 없습니다.");
			}
			
			if(deployCnt >= 1) {
				System.out.println("★ 이번 빌드에서 제외된 소스를 찾습니다.....");
				List<DeployLogDto> exptSrcList = deployLogDao.getExceptedSource(paramMap); // 기존에서 삭제된 목록
				int exptSrcCnt = exptSrcList.size();
				if(exptSrcCnt > 0) {
					System.out.println("★ " + exptSrcCnt + "개의 제외된 소스를 찾았습니다.");
					for(int i = 0; i < exptSrcList.size(); i++) {
						logger.debug(exptSrcList.get(i).getSvnSrcUrl() + "/" 
								+ exptSrcList.get(i).getSvnSrcNm() + " " 
								+ exptSrcList.get(i).getSvnSrcRevNum());
					}				
					scm.except(exptSrcList, prpt);
					
					String rollbackMsg = "DEPLOY_ID : " + param.getDepId() + "(" + (deployCnt+1) + ")\n" + "SVN롤백-빌드대상에서 제외됨";
					String rbReturnMsg = scm.commit(baseTgtPath+"/"+prpt.getEclipseProjNm(), rollbackMsg);
					if(!"EMPTY COMMIT".equals(rbReturnMsg)) {
						System.out.println("★ SVN에 적용(롤백/삭제)을 완료하였습니다. " + rbReturnMsg);
						isDone = true;
					} else {
						System.out.println("★ SVN에 변경된 내용이 없습니다. " + rbReturnMsg);
					}					
				} else {
					System.out.println("★ 이번 빌드에서 제외된 소스가 없습니다.");
				}
			}
			
			System.out.println("★ 이번 빌드에서 추가/수정된 소스목록(merge 대상)을 조회합니다.");
			List<SourceDto> newSrcList = requestDao.getNewSourceList(paramMap);
			if(newSrcList != null && newSrcList.size() > 0) {				
				for(int x = 0; x < newSrcList.size(); x++) {
					SourceDto src = newSrcList.get(x);
					System.out.println("★ " + src.getSvnSrcUrl().replaceFirst(prpt.getBaseSrcUrl() + "/", "") + "/" + src.getSvnSrcNm() + " " + src.getSvnSrcRevNum());
				}
				System.out.println("★ " + newSrcList.size() + "개의 추가/수정된 소스목록을 찾았습니다.");
			} else {
				System.out.println("★ 이번 빌드에서 추가/수정된 소스가 없습니다.");
			}
			
			//scm.merge(depSrcList, prpt); // 머지(전체)
			scm.merge(newSrcList, prpt); // 머지(부분)
			
			String commitMsg = "DEPLOY_ID : " + param.getDepId() + "(" + (deployCnt+1) + ")";
			String returnMsg = scm.commit(baseTgtPath+"/"+prpt.getEclipseProjNm(), commitMsg);  // 커밋
			if(!"EMPTY COMMIT".equals(returnMsg)) {
				System.out.println("★ SVN에 적용(추가/수정)을 완료하였습니다. " + returnMsg);
				isDone = true;
			} else {
				System.out.println("★ SVN에 변경된 내용이 없습니다. " + returnMsg);
			}
			
			if(isDone) {
				System.out.println("★ 배포이력을 기록했습니다. " + deployLogDao.addDeployedSourceLog(depSrcList, paramMap));
			}
		}		
		System.out.println("★ 배포ID " + param.getDepId() + "의  " + (deployCnt+1) + "번째 빌드를 종료합니다.");
	}
}
