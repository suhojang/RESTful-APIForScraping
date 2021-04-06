package project.service.impl;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * 
 * Agent정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
@Mapper("AGT_AGT_Mapper")
public interface AGT_AGT_Mapper {

	/**
	 * Agent정보 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> AGT_AGT_S1000J(Map<String,Object> param) throws Exception;

	/**
	 * Agent정보 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> AGT_AGT_S1001J(Map<String,Object> param) throws Exception;
	
	/**
	 * Agent정보조회 - Agent Id
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> AGT_AGT_V1000J(Map<String, Object> param) throws Exception;

	/**
	 * Agent정보 등록
	 * @param param
	 * @throws Exception
	 */
	void AGT_AGT_I1000J(Map<String, Object> param) throws Exception;

	/**
	 * Agent정보 수정
	 * @param param
	 * @throws Exception
	 */
	void AGT_AGT_U1000J(Map<String, Object> param) throws Exception;

	/**
	 * Agent정보 삭제
	 * @param param
	 * @throws Exception
	 */
	void AGT_AGT_D1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * 인증서 정보 삭제
	 * @param param
	 * @throws Exception
	 */
	void AGT_AGT_D1001J(Map<String, Object> param) throws Exception;

	/**
	 * Agent Master key 추가
	 * @param param
	 * @throws Exception
	 */
	void AGT_AGT_U1001J(Map<String, Object> param) throws Exception;

}
