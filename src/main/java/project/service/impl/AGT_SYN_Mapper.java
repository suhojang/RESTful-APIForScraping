package project.service.impl;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * 
 * Agent정보 동기화
 * @author 장수호
 *
 */
@Mapper("AGT_SYN_Mapper")
public interface AGT_SYN_Mapper {
	/**
	 * Agent정보 동기화
	 * @param param
	 * @throws Exception
	 */
	void AGT_SYN_U1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * 인증서정보 삭제
	 * @param param
	 * @throws Exception
	 */
	void AGT_SYN_D1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * 인증서정보 추가
	 * @param param
	 * @throws Exception
	 */
	void AGT_SYN_I1000J(Map<String, Object> param) throws Exception;

	/**
	 * 인증서정보 조회
	 * @param param
	 * @throws Exception
	 */
	List<Map<String, Object>> AGT_SYN_V1000J(Map<String, Object> param) throws Exception;
}
