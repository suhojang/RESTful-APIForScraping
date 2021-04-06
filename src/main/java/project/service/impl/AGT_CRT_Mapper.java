package project.service.impl;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * 
 * 인증서정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
@Mapper("AGT_CRT_Mapper")
public interface AGT_CRT_Mapper {

	/**
	 * 인증서정보 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> AGT_CRT_S1000J(Map<String,Object> param) throws Exception;

	/**
	 * 인증서정보조회 - 인증서 Id로 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> AGT_CRT_V1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * 인증서정보조회 - 인증서 Id로 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> AGT_CRT_V1001J(Map<String, Object> param) throws Exception;

	/**
	 * 인증서정보 등록
	 * @param param
	 * @throws Exception
	 */
	void AGT_CRT_I1000J(Map<String, Object> param) throws Exception;

	/**
	 * 인증서정보 수정
	 * @param param
	 * @throws Exception
	 */
	void AGT_CRT_U1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * 인증서정보 수정 - 전체 사용여부 : N
	 * @param param
	 * @throws Exception
	 */
	void AGT_CRT_U1001J(Map<String, Object> param) throws Exception;

	/**
	 * 인증서정보 삭제
	 * @param param
	 * @throws Exception
	 */
	void AGT_CRT_D1000J(Map<String, Object> param) throws Exception;
	
	
}
