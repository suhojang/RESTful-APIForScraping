package project.service.impl;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * 
 * Batch정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
@Mapper("AGT_BAT_Mapper")
public interface AGT_BAT_Mapper {

	/**
	 * Batch정보 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> AGT_BAT_S1000J(Map<String,Object> param) throws Exception;

	/**
	 * Batch정보조회 - Task Id로 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> AGT_BAT_V1000J(Map<String, Object> param) throws Exception;

	/**
	 * Batch정보 등록
	 * @param param
	 * @throws Exception
	 */
	void AGT_BAT_I1000J(Map<String, Object> param) throws Exception;

	/**
	 * Batch정보 수정
	 * @param param
	 * @throws Exception
	 */
	void AGT_BAT_U1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * Batch정보 구동시간 등록
	 * @param param
	 * @throws Exception
	 */
	void AGT_BAT_U1001J(Map<String, Object> param) throws Exception;

	/**
	 * Batch정보 삭제
	 * @param param
	 * @throws Exception
	 */
	void AGT_BAT_D1000J(Map<String, Object> param) throws Exception;
	
}
