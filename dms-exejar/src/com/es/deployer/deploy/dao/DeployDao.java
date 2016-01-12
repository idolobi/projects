package com.es.deployer.deploy.dao;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.es.deployer.common.Const;
import com.es.deployer.deploy.dto.DeployDto;
import com.es.deployer.deploy.dto.MailMessageDto;

public class DeployDao {
	private static Logger logger = Logger.getLogger(DeployDao.class);
			
	private SqlMapClientTemplate sqlMapClientTemplate;
	
	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
		this.sqlMapClientTemplate = sqlMapClientTemplate;
	}
	
	/**
	 * 정기배포용 배포ID를 생성하여 리턴한다.
	 * @param projId
	 * @param srcGrupCd
	 * @return
	 */
	public String makeRglrDepId(String projId, String srcGrupCd) {
		logger.debug("# Method Name : makeRglrDepId");
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", projId);
		paramMap.put("srcGrupCd", srcGrupCd);
		
		return (String)sqlMapClientTemplate.queryForObject("Deploy.SelectNewDeployId", paramMap);
	}
	
	/**
	 * 이미 등록된 배포ID가 있으면 true, 없으면 false를 리턴한다.
	 * @param projId
	 * @param depId
	 * @return
	 */
	public boolean existDepId(String projId, String depId) {
		logger.debug("# Method Name : existDepId");
		boolean exist = false;
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", projId);
		paramMap.put("depId", depId);
		
		int cnt = (Integer)sqlMapClientTemplate.queryForObject("Deploy.SelectCountDeployId", paramMap);
		if(cnt == 1) {
			exist = true;
		}
		return exist;
	}
	
	/**
	 * 배포ID 및 이에 해당하는 정보를 추가한다.
	 * @param deployDto
	 */
	public void registDepId(DeployDto deployDto) {
		logger.debug("# Method Name : registDepId");
		sqlMapClientTemplate.insert("Deploy.InsertDeploy", deployDto);
	}
	
	/**
	 * 잠금(마감) 상태로 변경한다.
	 * @param projId
	 * @param depId
	 */
	public void lock(String projId, String depId, String phase) {
		logger.debug("# Method Name : lock");
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", projId);
		paramMap.put("depId", depId);
		if(Const.PHASE_DVLP.equals(phase)) {
			paramMap.put("dvlpFnshYn", "Y"); // 초기엔 무조건 Y
		} else {
			paramMap.put("fnshYn", "Y"); // 초기엔 무조건 Y
		}
		paramMap.put("lastModiUsrId", "hudson");
		sqlMapClientTemplate.update("Deploy.UpdateFnshYn", paramMap);
	}
	
	/**
	 * 잠금해제(마감해제) 상태로 변경한다.
	 * @param projId
	 * @param depId
	 */
	public void unlock(String projId, String depId, String phase) {
		logger.debug("# Method Name : unlock");
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", projId);
		paramMap.put("depId", depId);
		if(Const.PHASE_DVLP.equals(phase)) {
			paramMap.put("dvlpFnshYn", "N"); 
		} else {
			paramMap.put("fnshYn", "N");
		}
		paramMap.put("lastModiUsrId", "hudson");
		sqlMapClientTemplate.update("Deploy.UpdateFnshYn", paramMap);
	}
	
	/**
	 * 잠금(마감) 여부를 확인한다.
	 * @param projId
	 * @param depId
	 * @return
	 */
	public boolean isLocked(String projId, String depId, String phase) {
		logger.debug("# Method Name : isLocked");
		boolean islocked = false;
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", projId);
		paramMap.put("depId", depId);
		paramMap.put("phase", phase);
		
		int cnt = (Integer)sqlMapClientTemplate.queryForObject("Deploy.SelectCountLockedDeployId", paramMap);
		if(cnt == 1) {
			islocked = true;
		}
		return islocked;
	}

	/**
	 * 발송할 메일 메세지 조회(빌드 성공용)
	 * @param projId
	 * @param srcGrupCd
	 * @return
	 */
	public MailMessageDto selectTestDeployMessage(String projId, String srcGrupCd, String depId) {
		logger.debug("# Method Name : selectTestDeployMessage");
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", projId);
		paramMap.put("srcGrupCd", srcGrupCd);
		paramMap.put("depId", depId);
		
		return (MailMessageDto) sqlMapClientTemplate.queryForObject("Deploy.SelectTestDeployMessage", paramMap);
	}
	
	/**
	 * 발송할 메일 메세지 조회(빌드 실패용)
	 * @param projId
	 * @param srcGrupCd
	 * @param msgType
	 * @return
	 */
	public MailMessageDto selectTestDeployFailMessage(String projId, String srcGrupCd, String msgType) {
		logger.debug("# Method Name : selectTestDeployFailMessage");
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", projId);
		paramMap.put("srcGrupCd", srcGrupCd);
		paramMap.put("msgType", msgType);
		
		return (MailMessageDto) sqlMapClientTemplate.queryForObject("Deploy.SelectTestDeployFailMessage", paramMap);
	}
	
	/**
	 * 배포정보
	 * @param projId
	 * @param depId
	 * @return
	 */
	public DeployDto selectDeployInfo(String projId, String depId) {
		logger.debug("# Method Name : selectDeployInfo");
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", projId);
		paramMap.put("depId", depId);
		
		return (DeployDto) sqlMapClientTemplate.queryForObject("Deploy.SelectDeployInfo", paramMap);
	}

}
