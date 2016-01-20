package com.es.deployer.deploy.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.es.deployer.common.Const;
import com.es.deployer.deploy.dto.BaseRequestDto;
import com.es.deployer.deploy.dto.RequestCountDto;
import com.es.deployer.deploy.dto.SourceDto;
import com.es.deployer.doc.dto.StatusDto;

public class RequestDao {
	private static Logger logger = Logger.getLogger(RequestDao.class);
	
	private SqlMapClientTemplate sqlMapClientTemplate;

	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
		this.sqlMapClientTemplate = sqlMapClientTemplate;
	}

	/**
	 * 대상 소스 목록 조회
	 * @param paramMap : projId, depId, depPhsDivCd, phsCd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<SourceDto> getSvnSource(HashMap<String, String> paramMap) throws Exception {
		List<SourceDto> rtnList = new ArrayList<SourceDto>();
		if(Const.PHASE_TEST_CD.equals(paramMap.get("depPhsDivCd"))) {
			rtnList = (List<SourceDto>) sqlMapClientTemplate.queryForList("Request.SelectSvnSourceForTest", paramMap);
		}
		if(Const.PHASE_REAL_CD.equals(paramMap.get("depPhsDivCd"))) {
			rtnList = (List<SourceDto>) sqlMapClientTemplate.queryForList("Request.SelectSvnSourceForReal", paramMap);
		}
		return rtnList;
	}
	
	/**
	 * 새로 등록된 소스 목록 조회
	 * @param paramMap
	 * @return
	 * @throws DataAccessException
	 */
	@SuppressWarnings("unchecked")
	public List<SourceDto> getNewSourceList(HashMap<String, String> paramMap) throws DataAccessException {
		return (List<SourceDto>) sqlMapClientTemplate.queryForList("Request.SelectNewSourceList", paramMap);
	}
	
	/**
	 * 공통 jar의 소스 갯수(구조 변경으로 사용 안함)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public int getCommonSourceCount(HashMap<String, String> paramMap) throws Exception {
		return (Integer) sqlMapClientTemplate.queryForObject("Request.SelectCommonSourceCount", paramMap);
	}
	
	/**
	 * 추가된 소스 목록
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SourceDto> getAddedSource(HashMap<String, String> paramMap) {
		logger.debug("Method : getAddedSource(HashMap<String, String> paramMap)");
		logger.debug(" Param projId = " + paramMap.get("projId"));
		logger.debug(" Param depId = " + paramMap.get("depId"));
		logger.debug(" Param depPhsDivCd = " + paramMap.get("depPhsDivCd"));
		logger.debug(" Param phsCd = " + paramMap.get("phsCd"));
		
		String qryId = "";
		if(Const.PHASE_TEST_CD.equals(paramMap.get("depPhsDivCd"))) {
			qryId = "Request.SelectNewSourceForTest";
		} else if(Const.PHASE_REAL_CD.equals(paramMap.get("depPhsDivCd"))) {
			qryId = "Request.SelectNewSourceForReal";
		} else {
			logger.debug("depPhsDivCd is not defined.");
		}
		return (List<SourceDto>) sqlMapClientTemplate.queryForList(qryId, paramMap);
	}
	
	/**
	 * 배포ID를 배포요청 건과 매핑시킨다.
	 * 파라메터 requestStatusDto 중 본 method에서 사용하는 값은 
	 * projId, srcGrupCd, depReqDate, rglrUrgntDivCd, depId, 
	 * phsCd, lastModiUsrId 이다
	 * @param paramMap
	 */
	public int linkDepId(StatusDto requestStatusDto) {
		String qryId = "Request.UpdateRequestDeployId";
		return (Integer)sqlMapClientTemplate.update(qryId, requestStatusDto);
	}
	
	/**
	 * 배포요청 문서의 상태를 추가한다.(문서의 상태는 이력을 남기기 위해 갱신하지 않고 추가한다.)
	 * 파라메터 requestStatusDto 중 본 method에서 사용하는 값은 
	 * projId, srcGrupCd, depReqDate, rglrUrgntDivCd, 
	 * phsCd, fwardPhsCd, firstInptUsrId, lastModiUsrId 이다
	 * @param requestStatusDto
	 */
	public void addReqDocStat(StatusDto requestStatusDto) {
		String qryId = "Request.InsertRequestDocumentStatus";
		sqlMapClientTemplate.insert(qryId, requestStatusDto);
	}
	
	
	/**
	 * 수취인 목록 조회
	 * @param projId
	 * @param depId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BaseRequestDto> selectRecipientsForMailMessage(String projId, String depId, String phsCd) {
		logger.debug("# Method Name : selectRecipientsForMailMessage");
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", projId);
		paramMap.put("depId", depId);
		paramMap.put("phsCd", phsCd);
		
		return (List<BaseRequestDto>)sqlMapClientTemplate.queryForList("Request.SelectRecipientsForMailMessage", paramMap);
	}
	
	/**
	 * 요청 상태인 배포요청 건 수
	 * @param projId
	 * @param srcGrupCd
	 * @param depReqDate
	 * @param rglrUrgntDivCd
	 * @return
	 */
	public int selectRequestStatusCount(String projId, String srcGrupCd, String depReqDate, 
			String rglrUrgntDivCd, String phase) throws DataAccessException {
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", projId);
		paramMap.put("srcGrupCd", srcGrupCd);
		paramMap.put("depReqDate", depReqDate);
		paramMap.put("rglrUrgntDivCd", rglrUrgntDivCd);
		if(("20150201_1".equals(projId) || "20150301_1".equals(projId)) 
				&& Const.PHASE_TEST.equals(phase)) { // LECS2, LECS3
			paramMap.put("phsCd", "27");
		} else {
			paramMap.put("phsCd", "12");
		}
		
		return (Integer) sqlMapClientTemplate.queryForObject("Request.SelectRequestStatusCount", paramMap);
	}
	
	/**
	 * LECS 요청 상태인 배포요청 건 수
	 * @param projId
	 * @param srcGrupCd
	 * @param depReqDate
	 * @param rglrUrgntDivCd
	 * @return
	 */
	public int selectRequestStatusCountForLecs(String projId, String srcGrupCd, String depReqDate, 
			String[] rglrUrgntDivCds, String[] depReqIds, String phase) throws DataAccessException {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projId", projId);
		paramMap.put("srcGrupCd", srcGrupCd);
		paramMap.put("depReqDate", depReqDate);
		paramMap.put("rglrUrgntDivCds", rglrUrgntDivCds);
		paramMap.put("depReqIds", depReqIds);
		if(("20150201_1".equals(projId) || "20150301_1".equals(projId)) 
				&& Const.PHASE_TEST.equals(phase)) { // LECS2, LECS3
			paramMap.put("phsCd", "27");
		} else {
			paramMap.put("phsCd", "12");
		}
		
		return (Integer) sqlMapClientTemplate.queryForObject("Request.SelectRequestStatusCount", paramMap);
	}
	
	/**
	 * 재요청, 재접수 건수 조회
	 * @param projId
	 * @param depId
	 * @return
	 * @throws DataAccessException
	 */
	@SuppressWarnings("unchecked")
	public List<RequestCountDto> selectRequestAndReceiptCount(String projId, String depId) throws DataAccessException {
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", projId);
		paramMap.put("depId", depId);
		return (List<RequestCountDto>) sqlMapClientTemplate.queryForList("Request.SelectRequestAndReceiptCount", paramMap);
	}
	
	/**
	 * 재요청된 배포요청 건에 배포ID 매핑
	 * @param requestStatusDto
	 * @return
	 * @throws DataAccessException
	 */
	public int updateReRequestDeployId(StatusDto statusDto) throws DataAccessException {
		return sqlMapClientTemplate.update("Request.UpdateReRequestDeployId", statusDto);
	}
	
	/**
	 * 재요청된 배포요청 건 문서상태 추가
	 * @param requestStatusDto
	 * @throws DataAccessException
	 */
	public void insertReRequestDocumentStatus(StatusDto statusDto) throws DataAccessException {
		sqlMapClientTemplate.insert("Request.InsertReRequestDocumentStatus", statusDto);
	}
	
	/**
	 * 접수 상태를 시험배포 상태로 변경
	 * @param requestStatusDto
	 * @return
	 */
	public int updatePhsProcMaxSeq(StatusDto requestStatusDto) {
		return sqlMapClientTemplate.update("Request.UpdatePhsProcMaxSeq", requestStatusDto);
	}
}
