package project.service.impl;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * 
 * Task정보 조회/등록 처리
 * @author 장수호
 *
 */
@Mapper("AGT_TSK_Mapper")
public interface AGT_TSK_Mapper {

	/**
	 * Task정보 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> AGT_TSK_S1000J(Map<String,Object> param) throws Exception;

	/**
	 * Task정보조회 - Task Id로 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> AGT_TSK_V1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * Task상태값 변경
	 * @param param
	 * @throws Exception
	 */
	void AGT_TSK_U1001J(Map<String, Object> param) throws Exception;

	/**
	 * Task결과 데이터 등록
	 * @param param
	 * @throws Exception
	 */
	void AGT_TSK_U1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * Task수행 데이터 등록
	 * @param param
	 * @throws Exception
	 */
	void AGT_TSK_I1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * Task정보 삭제
	 * @param param
	 * @throws Exception
	 */
	void AGT_TSK_D1000J(Map<String, Object> param) throws Exception;
	
}
