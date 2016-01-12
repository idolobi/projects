package com.es.deployer.deploy.service;

import java.util.List;

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
import com.es.deployer.deploy.dto.DeployDto;
import com.es.deployer.deploy.dto.RequestCountDto;
import com.es.deployer.doc.dao.StatusDao;
import com.es.deployer.doc.dto.StatusDto;

public class Accept {
	private static Logger logger = Logger.getLogger(Accept.class);
	
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
	 * 접수 - 일반 긴급은 접수하지 않음
	 * @param param
	 * @return
	 * @throws DeployerException
	 */
	public int acceptRequest(Parameter param) throws DeployerException {
		int returnValue = 0;
		
		final String phase = param.getPhase();
		Property prpt = new Property(param.getService()+"."+phase);
		
		final String projId = prpt.getProjId();
		final String srcGrupCd = prpt.getSrcGrupCd();
		
		String depId = "";
		String rglrUrgntDivCd = Const.DIV_REGULAR;
		String[] rglrUrgntDivCds = null;
		String[] depReqIds = null;
		if(Const.L2_PROJ_ID.equals(projId)) {
			depId = Const.L2_DEP_ID;
			rglrUrgntDivCds = param.getRglrUrgnt().length() >= 1 ? param.getRglrUrgnt().split(",") : null;
			depReqIds = param.getDepReqId().length() >= 10 ? param.getDepReqId().split(",") : null;
		} else if(Const.L3_PROJ_ID.equals(projId)) {
			depId = Const.L3_DEP_ID;
			rglrUrgntDivCds = param.getRglrUrgnt().length() >= 1 ? param.getRglrUrgnt().split(",") : null;
			depReqIds = param.getDepReqId().length() >= 10 ? param.getDepReqId().split(",") : null;
		} else {
			depId = deployDao.makeRglrDepId(projId, srcGrupCd);
		}		
		logger.debug("# depId : " + depId);
		
		final String depReqDate = splitDepDate(depId);
		final String usrId = Const.USR_DEPLOYER;
		final String docId = Const.DOC_REQUEST;
		
		if(Const.L2_PROJ_ID.equals(projId) || Const.L3_PROJ_ID.equals(projId)) {
			returnValue = acceptRequestForLecs(projId, srcGrupCd, depReqDate, rglrUrgntDivCds, depReqIds, 
					depId, phase, usrId, docId);
		} else {
			returnValue = acceptRequestForNorm(projId, srcGrupCd, depReqDate, rglrUrgntDivCd, depId, phase, usrId, docId);
		}
		
		return returnValue;
	}
	
	/**
	 * LECS용
	 * @param projId
	 * @param srcGrupCd
	 * @param depReqDate
	 * @param rglrUrgntDivCd
	 * @param depId
	 * @param phase
	 * @param usrId
	 * @param docId
	 * @return
	 * @throws DeployerException
	 */
	private int acceptRequestForLecs(final String projId, final String srcGrupCd, 
			final String depReqDate, final String[] rglrUrgntDivCds, final String[] depReqIds, 
			final String depId,	final String phase, final String usrId,	final String docId) 
					throws DeployerException {
		int rtn = 0;
		logger.debug("[LECS] Transaction Start...");
		rtn = transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				Integer savedCnt = 0;
				// LECS는 배포ID가 무조건 있어야 함
				// LECS는 마감기능을 사용하지 않음

				if(requestDao.selectRequestStatusCountForLecs(projId, srcGrupCd, depReqDate, rglrUrgntDivCds, depReqIds, phase) > 0) {
					// 상태 획득			
					String phsCd = statusDao.getPhsCdOnEvtNm(projId, docId, Const.EVT_NM_REQUEST);
					if(Const.PHASE_TEST.equals(phase)) { // 테스트 단계이면,
						phsCd = "27"; // 개발확인
					}
					String fwdPhsCd = statusDao.getFwdPhsCdOnPhsCd(projId, docId, phsCd);
					// 배포요청등록에 배포ID 매핑
					StatusDto requestStatusDto = new StatusDto();
					requestStatusDto.setProjId(projId);
					requestStatusDto.setSrcGrupCd(srcGrupCd);
					requestStatusDto.setDepReqDate(depReqDate);
					requestStatusDto.setRglrUrgntDivCds(rglrUrgntDivCds);
					requestStatusDto.setPhsCd(phsCd); // phsCd - 요청
					requestStatusDto.setFwardPhsCd(fwdPhsCd); // fwardPhsCd - 접수
					requestStatusDto.setDepId(depId);
					requestStatusDto.setDocId(docId);
					requestStatusDto.setFirstInptUsrId(usrId);
					requestStatusDto.setLastModiUsrId(usrId);
					int cnt = requestDao.linkDepId(requestStatusDto);
					logger.info("프로젝트("+projId+")의 배포ID("+depId+")를 "+cnt+"건의 배포요청에 매핑하였습니다.");
					// 배포요청등록에 상태 추가
					if(cnt > 0) {						
						requestDao.updatePhsProcMaxSeq(requestStatusDto);
						requestDao.addReqDocStat(requestStatusDto);						
						logger.info("프로젝트("+projId+")의 배포ID("+depId+")를 매핑된 배포 건의 상태를 변경하였습니다.");
					}			
				} else {
					savedCnt = -1;
				}
				
				return savedCnt;
			}
		});
		
		logger.debug("[LECS] Transaction End.");
			
		if(rtn == -1) {
			throw new DeployerException("요청한 LECS 배포 건이 없습니다.");
		}	
		
		return rtn;
	}
	
	/**
	 * 일반(LECS 제외)
	 * @param projId
	 * @param srcGrupCd
	 * @param depReqDate
	 * @param rglrUrgntDivCd
	 * @param depId
	 * @param phase
	 * @param usrId
	 * @param docId
	 * @return
	 * @throws DeployerException
	 */
	private int acceptRequestForNorm(final String projId, final String srcGrupCd, 
			final String depReqDate, final String rglrUrgntDivCd, final String depId, 
			final String phase, final String usrId,	final String docId) throws DeployerException {
		int rtn = 0;
		
		logger.debug("Transaction Start...");
		rtn = transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				Integer savedCnt = 0;
				// 배포 마스터 데이터 생성(무조건 생성)
				if(!deployDao.existDepId(projId, depId)) {
					logger.info("프로젝트("+projId+")의 배포ID("+depId+")가 존재하지 않음");
					DeployDto deployDto = new DeployDto();
					deployDto.setProjId(projId);
					deployDto.setDepId(depId);
					deployDto.setSrcGrupCd(srcGrupCd);
					deployDto.setDepDate(depReqDate);
					deployDto.setRglrUrgntDivCd(rglrUrgntDivCd); // 정기
					if(Const.PHASE_DVLP.equals(phase)) { // lecs용인데... 실제로 사용할 일이 없음... 나중에 정리(삭제) 필요!
						deployDto.setDvlpFnshYn("N"); // lecs는 모두 N -- lecs는 마감 기능을 사용하지 않음
						deployDto.setFnshYn("N");
					} else {
						deployDto.setDvlpFnshYn("N"); // lecs를 제외하면 개발단계를 사용하지 않으므로 N
						deployDto.setFnshYn("Y"); // 초기엔 무조건 Y
					}						
					deployDto.setFirstInptUsrId(usrId);
					deployDto.setLastModiUsrId(usrId);
					
					deployDao.registDepId(deployDto);
					logger.info("프로젝트("+projId+")의 배포ID("+depId+")를 등록하였습니다.");
				} else {
					// 마감이 안된 경우 마감처리
					if(!deployDao.isLocked(projId, depId, phase)) {
						deployDao.lock(projId, depId, phase);
					} else {
						logger.info("프로젝트("+projId+")의 배포ID("+depId+")는 이미 잠겨있는 상태입니다.");
					}
				}
									
				if(requestDao.selectRequestStatusCount(projId, srcGrupCd, depReqDate, rglrUrgntDivCd, phase) > 0) {
					// 상태 획득			
					String phsCd = statusDao.getPhsCdOnEvtNm(projId, docId, Const.EVT_NM_REQUEST);
					String fwdPhsCd = statusDao.getFwdPhsCdOnPhsCd(projId, docId, phsCd);
					// 배포요청등록에 배포ID 매핑
					StatusDto requestStatusDto = new StatusDto();
					requestStatusDto.setProjId(projId);
					requestStatusDto.setSrcGrupCd(srcGrupCd);
					requestStatusDto.setDepReqDate(depReqDate);
					requestStatusDto.setRglrUrgntDivCd(rglrUrgntDivCd);
					requestStatusDto.setPhsCd(phsCd); // phsCd - 요청
					requestStatusDto.setFwardPhsCd(fwdPhsCd); // fwardPhsCd - 접수
					requestStatusDto.setDepId(depId);
					requestStatusDto.setDocId(docId);
					requestStatusDto.setFirstInptUsrId(usrId);
					requestStatusDto.setLastModiUsrId(usrId);
					int cnt = requestDao.linkDepId(requestStatusDto);
					logger.info("프로젝트("+projId+")의 배포ID("+depId+")를 "+cnt+"건의 배포요청에 매핑하였습니다.");
					// 배포요청등록에 상태 추가
					if(cnt > 0) {						
						requestDao.updatePhsProcMaxSeq(requestStatusDto);
						requestDao.addReqDocStat(requestStatusDto);						
						logger.info("프로젝트("+projId+")의 배포ID("+depId+")를 매핑된 배포 건의 상태를 변경하였습니다.");
					}			
				} else {
					savedCnt = -1;
				}
				
				return savedCnt;
			}			
		});
		
		logger.debug("Transaction End.");
		
		if(rtn == -1) {
			throw new DeployerException("요청한 배포 건이 없습니다.");
		}		
		
		return rtn;
	}
	
	/**
	 * 자동 접수 처리(정기배포 전용)
	 * @deprecated
	 * @param param
	 * @return
	 * @throws DeployerException
	 */
	public int acceptRegularRequest(Parameter param) throws DeployerException {
		int rtn = 0;
		
		final String phase = param.getPhase();
		Property prpt = new Property(param.getService()+"."+phase);
		
		final String projId = prpt.getProjId();
		final String srcGrupCd = prpt.getSrcGrupCd();
		String tmpDepId = deployDao.makeRglrDepId(projId, srcGrupCd); 
		if(Const.L2_PROJ_ID.equals(projId)) {
			tmpDepId = "L2_00000000_0000";
		} else if(Const.L3_PROJ_ID.equals(projId)) {
			tmpDepId = "L3_00000000_0000";
		}
		final String depId = tmpDepId;		
		logger.debug("# depId : " + depId);
		final String depReqDate = splitDepDate(depId);
		final String rglrUrgntDivCd = Const.DIV_REGULAR;
		final String usrId = Const.USR_DEPLOYER;
		final String docId = Const.DOC_REQUEST;
		
		//if(requestDao.selectRequestStatusCount(projId, srcGrupCd, depReqDate, rglrUrgntDivCd) > 0) {
		logger.debug("Transaction Start...");
		rtn = transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				Integer savedCnt = 0;
				// 배포 마스터 데이터 생성(무조건 생성)
				if(!deployDao.existDepId(projId, depId)) {
					logger.info("프로젝트("+projId+")의 배포ID("+depId+")가 존재하지 않음");
					DeployDto deployDto = new DeployDto();
					deployDto.setProjId(projId);
					deployDto.setDepId(depId);
					deployDto.setSrcGrupCd(srcGrupCd);
					deployDto.setDepDate(depReqDate);
					deployDto.setRglrUrgntDivCd(rglrUrgntDivCd); // 정기
					if(Const.PHASE_DVLP.equals(phase)) { // lecs용인데... 실제로 사용할 일이 없음... 나중에 정리(삭제) 필요!
						deployDto.setDvlpFnshYn("N"); // lecs는 모두 N -- lecs는 마감 기능을 사용하지 않음
						deployDto.setFnshYn("N");
					} else {
						deployDto.setDvlpFnshYn("N"); // lecs를 제외하면 개발단계를 사용하지 않으므로 N
						deployDto.setFnshYn("Y"); // 초기엔 무조건 Y
					}						
					deployDto.setFirstInptUsrId(usrId);
					deployDto.setLastModiUsrId(usrId);
					
					deployDao.registDepId(deployDto);
					logger.info("프로젝트("+projId+")의 배포ID("+depId+")를 등록하였습니다.");
				} else {
					// 마감이 안된 경우 마감처리
					if(!deployDao.isLocked(projId, depId, phase)) {
						if(!(Const.L2_PROJ_ID.equals(projId) || Const.L3_PROJ_ID.equals(projId))) { // lecs는 마감을 사용하지 않음
							deployDao.lock(projId, depId, phase);
						} else {
							logger.info("프로젝트("+projId+")의 배포ID("+depId+")는 LECS용 이므로 잠금기능을 사용하지 않습니다.");
						}
					} else {
						logger.info("프로젝트("+projId+")의 배포ID("+depId+")는 이미 잠겨있는 상태입니다.");
					}
				}
									
				if(requestDao.selectRequestStatusCount(projId, srcGrupCd, depReqDate, rglrUrgntDivCd, phase) > 0) {
					// 상태 획득			
					String phsCd = statusDao.getPhsCdOnEvtNm(projId, docId, Const.EVT_NM_REQUEST);
					if((Const.L2_PROJ_ID.equals(projId) || Const.L3_PROJ_ID.equals(projId)) 
							&& Const.PHASE_TEST.equals(phase)) { // LECS2, LECS3
						phsCd = "27"; // 개발확인
					}
					String fwdPhsCd = statusDao.getFwdPhsCdOnPhsCd(projId, docId, phsCd);
					// 배포요청등록에 배포ID 매핑
					StatusDto requestStatusDto = new StatusDto();
					requestStatusDto.setProjId(projId);
					requestStatusDto.setSrcGrupCd(srcGrupCd);
					requestStatusDto.setDepReqDate(depReqDate);
					requestStatusDto.setRglrUrgntDivCd(rglrUrgntDivCd);
					requestStatusDto.setPhsCd(phsCd); // phsCd - 요청
					requestStatusDto.setFwardPhsCd(fwdPhsCd); // fwardPhsCd - 접수
					requestStatusDto.setDepId(depId);
					requestStatusDto.setDocId(docId);
					requestStatusDto.setFirstInptUsrId(usrId);
					requestStatusDto.setLastModiUsrId(usrId);
					int cnt = requestDao.linkDepId(requestStatusDto);
					logger.info("프로젝트("+projId+")의 배포ID("+depId+")를 "+cnt+"건의 배포요청에 매핑하였습니다.");
					// 배포요청등록에 상태 추가
					if(cnt > 0) {						
						requestDao.updatePhsProcMaxSeq(requestStatusDto);
						requestDao.addReqDocStat(requestStatusDto);						
						logger.info("프로젝트("+projId+")의 배포ID("+depId+")를 매핑된 배포 건의 상태를 변경하였습니다.");
					}			
				} else {
					savedCnt = -1;
				}
				
				return savedCnt;
			}			
		});
		logger.debug("Transaction End.");
		
		if(rtn == -1) {
			throw new DeployerException("요청한 배포 건이 없습니다.");
		}
		//} else {
		//	throw new DeployerException("요청한 배포 건이 없습니다.");
		//}
		return rtn;
	}
	
	/**
	 * 정기배포 중 재요청 건 접수 처리
	 * @param param
	 * @return
	 * @throws DeployerException
	 */
	public int acceptRegularReRequest(Parameter param) throws DeployerException {
		int rtn = 0;
		
		final String phase = param.getPhase();
		Property prpt = new Property(param.getService()+"."+phase);
		
		final String projId = prpt.getProjId();
		final String srcGrupCd = prpt.getSrcGrupCd();
		final String depId = deployDao.makeRglrDepId(projId, srcGrupCd);	
		final String depReqDate = splitDepDate(depId);
		final String rglrUrgntDivCd = Const.DIV_REGULAR;
		final String usrId = Const.USR_DEPLOYER;
		final String docId = Const.DOC_REQUEST;
	
		logger.debug("Transaction Start...");
		rtn = transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				Integer savedCnt = 0;
				
				if(deployDao.existDepId(projId, depId)) {
					if(!deployDao.isLocked(projId, depId, phase)) {
						deployDao.lock(projId, depId, phase);
					} else {
						logger.info("프로젝트("+projId+")의 배포ID("+depId+")는 이미 잠겨있는 상태입니다.");
					}
				} else {
					logger.error("배포ID가 생성되지 않아 접수가 불가능합니다.");
				}
									
				if(requestDao.selectRequestStatusCount(projId, srcGrupCd, depReqDate, rglrUrgntDivCd, phase) > 0) {
					// 상태 획득			
					String phsCd = statusDao.getPhsCdOnEvtNm(projId, docId, Const.EVT_NM_REQUEST);
					if((Const.L2_PROJ_ID.equals(projId) || Const.L3_PROJ_ID.equals(projId)) 
							&& Const.PHASE_TEST.equals(phase)) { // LECS2, LECS3
						phsCd = "27";
					}
					String fwdPhsCd = statusDao.getFwdPhsCdOnPhsCd(projId, docId, phsCd);
					// 배포요청등록에 배포ID 매핑
					StatusDto requestStatusDto = new StatusDto();
					requestStatusDto.setProjId(projId);
					requestStatusDto.setSrcGrupCd(srcGrupCd);
					requestStatusDto.setDepReqDate(depReqDate);
					requestStatusDto.setRglrUrgntDivCd(rglrUrgntDivCd);
					requestStatusDto.setPhsCd(phsCd); // phsCd - 요청
					requestStatusDto.setFwardPhsCd(fwdPhsCd); // fwardPhsCd - 접수
					requestStatusDto.setDepId(depId);
					requestStatusDto.setDocId(docId);
					requestStatusDto.setFirstInptUsrId(usrId);
					requestStatusDto.setLastModiUsrId(usrId);
					int cnt = requestDao.updateReRequestDeployId(requestStatusDto);
					logger.info("프로젝트("+projId+")의 배포ID("+depId+")를 "+cnt+"건의 배포요청에 매핑하였습니다.");
					// 배포요청등록에 상태 추가
					if(cnt > 0) {
						requestDao.insertReRequestDocumentStatus(requestStatusDto);
						logger.info("프로젝트("+projId+")의 배포ID("+depId+")를 매핑된 배포 건의 상태를 변경하였습니다.");
					}			
				} else {
					savedCnt = -1;
				}
				
				return savedCnt;
			}			
		});
		logger.debug("Transaction End.");
		
		if(rtn == -1) {
			throw new DeployerException("요청한 배포 건이 없습니다.");
		}
		return rtn;
	}
	
	/**
	 * 자동 접수 처리(특정 배포ID)
	 * @param param
	 * @return
	 * @throws DeployerException
	 */
	public int acceptSpecialDeployRequest(Parameter param) throws DeployerException {
		int rtn = 0;
		
		final String phase = param.getPhase();
		Property prpt = new Property(param.getService()+"."+phase);
		
		final String projId = prpt.getProjId();
		final String srcGrupCd = prpt.getSrcGrupCd();
		final String depId = param.getDepId();
		logger.debug("# depId@acceptSpecialDeployRequest : " + depId);
		final String depReqDate = splitDepDate(depId);
		final String rglrUrgntDivCd = depId.indexOf("_1000") > 0 ? Const.DIV_REGULAR : Const.DIV_URGENT;
		final String usrId = Const.USR_DEPLOYER;
		final String docId = Const.DOC_REQUEST;
				
		//if(requestDao.selectRequestStatusCount(projId, srcGrupCd, depReqDate, rglrUrgntDivCd, phase) > 0) {		
		if(count(projId, depId) > 0) {
			if(!depId.equals(param.getDepId())) {
				throw new DeployerException("배포ID가 일치하지 않습니다.");
			}
			
			logger.debug("Transaction Start...");
			rtn = transactionTemplate.execute(new TransactionCallback<Integer>() {
				@Override
				public Integer doInTransaction(TransactionStatus status) {
					Integer savedCnt = 0;
					if(!deployDao.existDepId(projId, depId)) {
						logger.info("프로젝트("+projId+")의 배포ID("+depId+")가 존재하지 않음");
						DeployDto deployDto = new DeployDto();
						deployDto.setProjId(projId);
						deployDto.setDepId(depId);
						deployDto.setSrcGrupCd(srcGrupCd);
						deployDto.setDepDate(depReqDate);
						deployDto.setRglrUrgntDivCd(rglrUrgntDivCd); // 정기
						if(Const.PHASE_DVLP.equals(phase)) {
							deployDto.setDvlpFnshYn("N"); // lecs는 무조건 Y
							deployDto.setFnshYn("N");
						} else {
							deployDto.setDvlpFnshYn("N");
							deployDto.setFnshYn("Y"); // 초기엔 무조건 Y
						}	
						deployDto.setFirstInptUsrId(usrId);
						deployDto.setLastModiUsrId(usrId);
						
						deployDao.registDepId(deployDto);
						logger.info("프로젝트("+projId+")의 배포ID("+depId+")를 등록하였습니다.");
					}
					if(!deployDao.isLocked(projId, depId, phase)) {
						deployDao.lock(projId, depId, phase);
					} else {
						logger.info("프로젝트("+projId+")의 배포ID("+depId+")는 이미 잠겨있는 상태입니다.");
					}
									
					String phsCd = statusDao.getPhsCdOnEvtNm(projId, docId, Const.EVT_NM_REQUEST);
					if((Const.L2_PROJ_ID.equals(projId) || Const.L3_PROJ_ID.equals(projId)) 
							&& Const.PHASE_TEST.equals(phase)) { // LECS2, LECS3
						phsCd = "27";
					}
					String fwdPhsCd = statusDao.getFwdPhsCdOnPhsCd(projId, docId, phsCd);
					
					StatusDto requestStatusDto = new StatusDto();
					requestStatusDto.setProjId(projId);
					requestStatusDto.setSrcGrupCd(srcGrupCd);
					requestStatusDto.setDepReqDate(depReqDate);
					requestStatusDto.setRglrUrgntDivCd(rglrUrgntDivCd);
					requestStatusDto.setPhsCd(phsCd); // phsCd - 요청
					requestStatusDto.setFwardPhsCd(fwdPhsCd); // fwardPhsCd - 접수
					requestStatusDto.setDepId(depId);
					requestStatusDto.setDocId(docId);
					requestStatusDto.setFirstInptUsrId(usrId);
					requestStatusDto.setLastModiUsrId(usrId);
					
					int cnt = requestDao.linkDepId(requestStatusDto);
					logger.info("프로젝트("+projId+")의 배포ID("+depId+")를 "+cnt+"건의 배포요청에 매핑하였습니다.");
					
					if(cnt > 0) {
						requestDao.addReqDocStat(requestStatusDto);
						requestDao.updatePhsProcMaxSeq(requestStatusDto);
						logger.info("프로젝트("+projId+")의 배포ID("+depId+")를 매핑된 배포 건의 상태를 변경하였습니다.");
					}			
					
					return savedCnt;
				}			
			});
			logger.debug("Transaction End.");
		} else {
			throw new DeployerException("요청한 배포 건이 없습니다.");
		}
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
	
	private int count(String projId, String depId) {
		List<RequestCountDto> lstCnt = requestDao.selectRequestAndReceiptCount(projId, depId);
		int rqstCnt = 0;
		int rcptCnt = 0;
		if(lstCnt != null) {
			for(int i = 0; i < lstCnt.size(); i++) {
				RequestCountDto cnt = lstCnt.get(i);
				rqstCnt = rqstCnt + cnt.getReqCnt(); 
				rcptCnt = rcptCnt + cnt.getRcptCnt(); 
			}
		}
		return rqstCnt + rcptCnt;
	}
}
