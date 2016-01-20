package com.es.deployer.doc.dao;

import java.util.HashMap;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class StatusDao {
	private SqlMapClientTemplate sqlMapClientTemplate;

	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
		this.sqlMapClientTemplate = sqlMapClientTemplate;
	}

	/**
	 * 이벤트 이름에 맞는 코드를 가져온다.
	 * @param projId
	 * @param docId
	 * @param evtNm
	 * @return phsCd
	 */
	public String getPhsCdOnEvtNm(String projId, String docId, String evtNm) {
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", projId);
		paramMap.put("docId", docId);
		paramMap.put("evtNm", evtNm);
		return (String) sqlMapClientTemplate.queryForObject("Status.SelectPhsCdOnEvtNm", paramMap);
	}
	
	/** LECS를 위한 임시 method
	 * 그냥 30 리턴
	 * @return
	 */
	public String getPhsCdOnEvtNm() {
		return "30";
	}
	
	/**
	 * 현재 단계(phsCd)의 앞으로단계(fwdPhsCd)를 가져온다.
	 * @param projId
	 * @param docId
	 * @param phsCd
	 * @return fwdPhsCd
	 */
	public String getFwdPhsCdOnPhsCd(String projId, String docId, String phsCd) {
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", projId);
		paramMap.put("docId", docId);
		paramMap.put("phsCd", phsCd);
		return (String) sqlMapClientTemplate.queryForObject("Status.SelectFwdPhsCdOnPhsCd", paramMap);
	}
}
