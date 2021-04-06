package com.kwic.code;

public class ErrorCode {
	public static final String _000	= "000";
	/**
	 * Access Token 없이 호출한 경우
	 * */
	public static final String _901	= "901";
	/**
	 * Access Token이 유효하지 않은 경우
	 */
	public static final String _902	= "902";
	/**
	 * 접근 불가능 IP로 서버에 API를 호출한 경우
	 */
	public static final String _903	= "903";
	/**
	 * 토큰정보와 IP정보가 유효하지 않은 경우
	 */
	public static final String _904	= "904";
	/**
	 * 필수 파라메터 누락함
	 */
	public static final String _905	= "905";
	/**
	 * Agent정보 등록 오류
	 */
	public static final String _911	= "911";
	/**
	 * Agent정보 수정 오류
	 */
	public static final String _912	= "912";
	/**
	 * Agent정보 삭제 오류
	 */
	public static final String _913	= "913";
	/**
	 * 인증서정보 등록 오류
	 */
	public static final String _921	= "921";
	/**
	 * 인증서정보 수정 오류
	 */
	public static final String _922	= "922";
	/**
	 * 인증서정보 삭제 오류
	 */
	public static final String _923	= "923";
	/**
	 * Agent Master Key 생성 오류
	 */
	public static final String _924	= "924";
	/**
	 * Agent정보 동기화 오류
	 */
	public static final String _932	= "932";
	/**
	 * 스케쥴 등록 오류
	 */
	public static final String _941	= "941";
	/**
	 * 스케쥴 수정 오류
	 */
	public static final String _942	= "942";
	/**
	 * 스케쥴 삭제 오류
	 */
	public static final String _943	= "943";
	/**
	 * Task 결과 데이터 등록 오류
	 */
	public static final String _951	= "951";
	/**
	 * 기타오류
	 */
	public static final String _999	= "999";
}
