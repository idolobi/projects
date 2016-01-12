package com.es.deployer.common;

public class Const {
	/* SVN 디렉토리 */
	public static final String TRUNK = "/trunk";
	public static final String TEST_BRANCHES = "/branches/TEST";
	public static final String REAL_BRANCHES = "/branches/REAL";
	public static final String TEST_TAGS = "/tags/TEST";
	public static final String REAL_TAGS = "/tags/REAL";
	
	/* SvnEntry.java에서 사용 */
	public static final String[] SVN_DIR_DIV = {
		TRUNK, TEST_BRANCHES, REAL_BRANCHES, TEST_TAGS, REAL_TAGS
	};
	
	/* 테스트전용배포, 정기/긴급배포 구분 코드로 hudson job을 불러오는 목적으로 사용됨 */
	public static final String PHASE_DVLP_CD = "1";
	public static final String PHASE_TEST_CD = "2";
	public static final String PHASE_REAL_CD = "3";
	
	/* 정기/긴급배포시 시험배포와 운영배포 구분 코드 */
	public static final String PHASE_DVLP = "dvlp";
	public static final String PHASE_TEST = "test";
	public static final String PHASE_REAL = "real";
	
	public static final String DIV_REGULAR = "R";
	public static final String DIV_URGENT  = "U";
	public static final String DIV_TEST    = "T";
	
	public static final String USR_HUDSON = "hudson";
	public static final String USR_DEPLOYER = "deployer";
	public static final String USR_SVN = "subversion";
	
	/**
	 * 문서ID
	 */
	public static final String DOC_REQUEST      = "DOC0001";
	public static final String DOC_REQUEST_TEST = "DOC0002";
	public static final String DOC_USER_INFO    = "DOC0003";
	
	/**
	 * 이벤트 이름
	 */
	public static final String EVT_NM_SAVE     = "SAVE"; //저장
	public static final String EVT_NM_REQUEST  = "REQUEST"; //요청
	public static final String EVT_NM_RECEIPT  = "RECEIPT"; //접수
	public static final String EVT_NM_REJECT   = "REJECT"; //반환
	public static final String EVT_NM_REWORK   = "REWORK"; 
	public static final String EVT_NM_TEST     = "TEST";
	public static final String EVT_NM_PASS     = "PASS";
	public static final String EVT_NM_REAL     = "REAL";
	
	/**
	 * 빌드 결과
	 */
	public static final String BUILD_SUCCESS = "SUCCESS";
	public static final String BUILD_FAIL    = "FAIL";
	
	public static final String DEPLOYER_HOME = "D:/deployer";
	
	/**
	 * LECS의 프로젝트 ID
	 */
	public static final String L2_PROJ_ID = "20150201_1";
	public static final String L3_PROJ_ID = "20150301_1";
	
	public static final String L2_DEP_ID = "L2_00000000_0000";
	public static final String L3_DEP_ID = "L3_00000000_0000";
}
