package com.ranranx.aolie.application.page.service;

import com.ranranx.aolie.application.page.dto.PageDetailDto;
import com.ranranx.aolie.application.page.dto.PageInfo;
import com.ranranx.aolie.application.page.dto.PageInfoDto;
import com.ranranx.aolie.core.common.CommonUtils;
import com.ranranx.aolie.core.common.Constants;
import com.ranranx.aolie.core.common.IdGenerator;
import com.ranranx.aolie.core.common.SessionUtils;
import com.ranranx.aolie.core.datameta.datamodel.BlockViewer;
import com.ranranx.aolie.core.datameta.datamodel.DmConstants;
import com.ranranx.aolie.core.datameta.datamodel.SchemaHolder;
import com.ranranx.aolie.core.datameta.datamodel.TableInfo;
import com.ranranx.aolie.core.datameta.dto.BlockViewDto;
import com.ranranx.aolie.core.datameta.dto.ReferenceDto;
import com.ranranx.aolie.core.ds.dataoperator.DataOperatorFactory;
import com.ranranx.aolie.core.ds.definition.*;
import com.ranranx.aolie.core.exceptions.InvalidException;
import com.ranranx.aolie.core.handler.param.condition.Criteria;
import com.ranranx.aolie.core.service.UIService;
import com.ranranx.aolie.core.tree.LevelProvider;
import com.ranranx.aolie.core.tree.SysCodeRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author xxl
 * @Description
 * @Date 2020/10/31 20:08
 * @Version V0.0.1
 **/
@Service
@Transactional(readOnly = true)
public class PageService {

    @Autowired
    private DataOperatorFactory factory;
    @Autowired
    private UIService uiService;

    @Autowired
    private SchemaHolder schemaHolder;

    /**
     * 取得页面列表
     *
     * @param schemaId
     * @return
     */
    public List<PageInfoDto> findPageInfos(Long schemaId) {
        QueryParamDefinition queryParamDefinition = new QueryParamDefinition();
        queryParamDefinition.setTableDtos(PageInfoDto.class);
        queryParamDefinition.appendCriteria().andEqualTo("schema_id", schemaId)
                .andEqualTo("version_code", SessionUtils.getLoginVersion());
        queryParamDefinition.addOrder(new FieldOrder(PageInfoDto.class, "lvl_code", true, 1));
        return factory.getDefaultDataOperator().select(queryParamDefinition, PageInfoDto.class);

    }

    /**
     * 取得一页面的详细配置信息
     *
     * @param pageId
     * @return
     */
    public List<PageDetailDto> findPageDetail(Long pageId) {
        QueryParamDefinition queryParamDefinition = new QueryParamDefinition();
        queryParamDefinition.setTableDtos(PageDetailDto.class);
        queryParamDefinition.appendCriteria().andEqualTo("page_id", pageId)
                .andEqualTo("version_code", SessionUtils.getLoginVersion());
        return factory.getDefaultDataOperator().select(queryParamDefinition, PageDetailDto.class);
    }

    /**
     * 取得一页面的详细配置信息
     *
     * @param pageId
     * @return
     */
    public PageInfo findPageInfo(Long pageId) {
        QueryParamDefinition queryParamDefinition = new QueryParamDefinition();
        queryParamDefinition.setTableDtos(PageDetailDto.class);
        queryParamDefinition.appendCriteria().andEqualTo("page_id", pageId)
                .andEqualTo("version_code", SessionUtils.getLoginVersion());
        List<PageDetailDto> lstDetail = factory.getDefaultDataOperator().select(queryParamDefinition, PageDetailDto.class);
        queryParamDefinition.setTableDtos(PageInfoDto.class);
        PageInfoDto pageInfoDto = factory.getDefaultDataOperator().selectOne(queryParamDefinition, PageInfoDto.class);
        PageInfo info = new PageInfo();
        info.setLstPageDetail(lstDetail);
        info.setPageInfoDto(pageInfoDto);
        return info;
    }

    /**
     * 取得一页面的所在方案
     *
     * @param pageId
     * @return
     */
    public long findPageSchemaId(Long pageId, String version) {
        QueryParamDefinition queryParamDefinition = new QueryParamDefinition();
        queryParamDefinition.setTableDtos(PageInfoDto.class);
        queryParamDefinition.appendCriteria().andEqualTo("page_id", pageId)
                .andEqualTo("version_code", version);
        PageInfoDto pageInfoDto = factory.getDefaultDataOperator().selectOne(queryParamDefinition, PageInfoDto.class);
        return pageInfoDto.getSchemaId();
    }


    /**
     * 增加页面
     *
     * @param pageName
     * @param schemaId
     * @param parentId
     * @return
     */
    @Transactional(readOnly = false)
    public long addPage(String pageName, Long schemaId, Long parentId) {
        if (CommonUtils.isEmpty(pageName) || pageName.trim().equals("")) {
            throw new InvalidException("页面名称不可以为空!");
        }
        if (schemaId == null || schemaId < 1) {
            throw new InvalidException("方案没有指定");
        }

        PageInfoDto dto = new PageInfoDto();
        dto.setPageId(IdGenerator.getNextId(PageInfoDto.class.getName()));
        dto.setPageName(pageName);
        dto.setSchemaId(schemaId);
        dto.setLayoutType(Constants.LayoutType.BORDER_LAYOUT);
        dto.setLvlCode(getNextCode(parentId, schemaId));
        dto.setCanDrag((short) 1);
        dto.setVersionCode(SessionUtils.getLoginVersion());
        InsertParamDefinition insertParamDefinition = new InsertParamDefinition();
        insertParamDefinition.setNeedConvertToUnderLine(true);
        insertParamDefinition.setObject(dto);
        factory.getDefaultDataOperator().insert(insertParamDefinition);
        return dto.getPageId();
    }

    private String getNextCode(Long parentId, Long schemaId) {
        String lvlParent = "";
        if (parentId != null && parentId > 0) {
            PageInfo pageInfo = findPageInfo(parentId);
            if (pageInfo != null) {
                lvlParent = pageInfo.getPageInfoDto().getLvlCode();
            }
        }
        LevelProvider provider = new LevelProvider(lvlParent);

        QueryParamDefinition queryParamDefinition = new QueryParamDefinition();
        queryParamDefinition.setTableDtos(PageInfoDto.class);
        Field field = new Field();
        field.setFieldName("lvl_code");
        field.setGroupType(Constants.GroupType.MAX);
        queryParamDefinition.addField(field);
        Criteria criteria = queryParamDefinition.appendCriteria().andEqualTo("schema_id", schemaId)
                .andCondition("length(lvl_code)=", provider.getFirstSubCode().length())
                .andEqualTo("version_code", SessionUtils.getLoginVersion());
        List<Map<String, Object>> lstData = factory.getDefaultDataOperator().select(queryParamDefinition);
        if (lstData == null || lstData.isEmpty()) {
            return lvlParent + "001";
        }
        String lvlCode = CommonUtils.getStringField(lstData.get(0), "lvl_code");
        if (CommonUtils.isEmpty(lvlCode)) {
            return lvlParent + "001";
        }
        provider.setSCurrentCode(lvlCode);
        return provider.getNextCode();
    }


    /**
     * 删除指定 页面信息
     *
     * @param pageId
     * @return
     */
    @Transactional(readOnly = false)
    public String deletePage(long pageId) {
        if (pageId < 1) {
            return "指定的数据不存在";
        }
        DeleteParamDefinition deleteParamDefinition = new DeleteParamDefinition();
        deleteParamDefinition.setTableDto(PageDetailDto.class);
        deleteParamDefinition.getCriteria().andEqualTo("page_id", pageId)
                .andEqualTo("version_code", SessionUtils.getLoginVersion());

        factory.getDefaultDataOperator().delete(deleteParamDefinition);
        deleteParamDefinition.setTableDto(PageInfoDto.class);
        factory.getDefaultDataOperator().delete(deleteParamDefinition);
        return null;
    }

    public List<Long> findPageRefTables(long pageId) {
        List<PageDetailDto> lstPageDetail = findPageDetail(pageId);
        if (lstPageDetail == null || lstPageDetail.isEmpty()) {
            return null;
        }
        List<Long> lstResult = new ArrayList();
        for (PageDetailDto dto : lstPageDetail) {
            //如果存在自定义的界面,则不再过滤
            if (dto.getViewId().equals(DmConstants.DispType.CUSTOM_UI_ID)) {
                return null;
            }
            if (dto.getViewType() == Constants.PageViewType.reference) {
                continue;
            } else if (dto.getViewType() == Constants.PageViewType.blockView) {
                BlockViewer viewerInfo = SchemaHolder.getViewerInfo(dto.getViewId(), dto.getVersionCode());
                TableInfo[] viewTables = viewerInfo.getViewTables();
                if (viewTables != null) {
                    for (int i = 0; i < viewTables.length; i++) {
                        Long tableId = viewTables[i].getTableDto().getTableId();
                        if (lstResult.indexOf(tableId) == -1) {
                            lstResult.add(tableId);
                        }
                    }
                }
            } else if (dto.getViewType() == Constants.PageViewType.page) {
                List<Long> lstSub = findPageRefTables(dto.getViewId());
                if (lstSub != null) {
                    for (Long tableId : lstSub) {
                        if (lstResult.indexOf(tableId) == -1) {
                            lstResult.add(tableId);
                        }
                    }
                }
            }

        }
        return lstResult;
    }

    /**
     * 保存页面信息
     *
     * @param pageInfo
     */
    @Transactional(readOnly = false)
    public void savePageFullInfo(PageInfo pageInfo) {
        deletePage(pageInfo.getPageInfoDto().getPageId());
        InsertParamDefinition insertParamDefinition = new InsertParamDefinition();
        insertParamDefinition.setNeedConvertToUnderLine(true);
        insertParamDefinition.setObject(pageInfo.getPageInfoDto());
        factory.getDefaultDataOperator().insert(insertParamDefinition);
        List<PageDetailDto> lstPageDetail = pageInfo.getLstPageDetail();

        if (lstPageDetail != null && lstPageDetail.size() > 0) {
            long pageId = pageInfo.getPageInfoDto().getPageId();
            long schemaId = pageInfo.getPageInfoDto().getSchemaId();
            String versionCode = SessionUtils.getLoginVersion();
            for (PageDetailDto dto : lstPageDetail) {
                dto.setPageId(pageId);
                dto.setSchemaId(schemaId);
                dto.setVersionCode(versionCode);
                if (dto.getPageDetailId() == null || dto.getPageDetailId() < 1) {
                    dto.setPageDetailId(IdGenerator.getNextId(PageDetailDto.class.getName()));
                }
            }
        }
        insertParamDefinition = new InsertParamDefinition();
        insertParamDefinition.setNeedConvertToUnderLine(true);
        insertParamDefinition.setObjects(pageInfo.getLstPageDetail());
        factory.getDefaultDataOperator().insert(insertParamDefinition);
    }

    /**
     * 更新页面的层次设置
     *
     * @param mapIdToCode
     */
    @Transactional(readOnly = false)
    public void updatePageLevel(Map<Long, String> mapIdToCode, long schemaId) {
        if (mapIdToCode == null || mapIdToCode.isEmpty()) {
            return;
        }
        UpdateParamDefinition updateParamDefinition;
        updateParamDefinition = new UpdateParamDefinition();
        updateParamDefinition.setSelective(true);
        updateParamDefinition.setTableNameByDto(PageInfoDto.class);
        updateParamDefinition.setIdField("page_id,schema_id,version_code");

        updateParamDefinition.setLstRows(toMap(mapIdToCode, schemaId));
        factory.getDefaultDataOperator().update(updateParamDefinition);
    }

    private List<Map<String, Object>> toMap(Map<Long, String> mapIdToCode, long schemaId) {
        Iterator<Map.Entry<Long, String>> iterator = mapIdToCode.entrySet().iterator();

        List<Map<String, Object>> lstResult = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<Long, String> next = iterator.next();
            Map<String, Object> map = new HashMap<>();
            map.put("version_code", SessionUtils.getLoginVersion());
            map.put("page_id", next.getKey());
            map.put("schema_id", schemaId);
            map.put("lvl_code", next.getValue());
            lstResult.add(map);
        }
        return lstResult;
    }

    /**
     * 查询所有的视图信息
     *
     * @param schemaId
     * @return
     */
    public List<Map<String, Object>> getPageElements(Long schemaId) {
        QueryParamDefinition queryParamDefinition = new QueryParamDefinition();
        queryParamDefinition.setTableDtos(BlockViewDto.class);
        queryParamDefinition.appendCriteria().andEqualTo("schema_id", schemaId)
                .andEqualTo("version_code", SessionUtils.getLoginVersion());

        List<PageInfoDto> lstPageDto = findPageInfos(schemaId);
        List<ReferenceDto> referenceDtos = schemaHolder.getReferenceDtos(SessionUtils.getLoginVersion());
        List<BlockViewDto> blocks = uiService.getBlockViews(schemaId);
        //将三个组装成树状结构
        List<Map<String, Object>> result = new ArrayList<>();
        addBlockInfo(blocks, result);
        addReferenceInfo(referenceDtos, result);
        addPageInfo(lstPageDto, result);
        return result;
    }

    /**
     * 增加页面信息
     *
     * @param lstPageDto
     * @param result
     */
    private void addPageInfo(List<PageInfoDto> lstPageDto, List<Map<String, Object>> result) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 3);
        map.put("code", "003");
        map.put("name", "页面");
        map.put("type", "3");
        result.add(map);
        if (lstPageDto != null && !lstPageDto.isEmpty()) {
            for (PageInfoDto dto : lstPageDto) {
                map = new HashMap<>();
                map.put("id", dto.getPageId());
                map.put("code", "003" + dto.getLvlCode());
                map.put("name", dto.getPageName());
                map.put("type", "3");
                result.add(map);
            }
        }
    }

    /**
     * 增加页面信息
     *
     * @param lstBlockDto
     * @param result
     */
    private void addBlockInfo(List<BlockViewDto> lstBlockDto, List<Map<String, Object>> result) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("code", "001");
        map.put("name", "视图");
        map.put("type", "1");
        result.add(map);
        map = new HashMap<>();
        map.put("id", 2);
        map.put("code", "001000");
        map.put("name", "自定义视图");
        map.put("type", "1");
        result.add(map);
        if (lstBlockDto != null && !lstBlockDto.isEmpty()) {
            for (BlockViewDto dto : lstBlockDto) {
                map = new HashMap<>();
                map.put("id", dto.getBlockViewId());
                map.put("code", "001" + dto.getLvlCode());
                map.put("name", dto.getBlockViewName());
                map.put("type", "1");
                result.add(map);
            }
        }
    }

    /**
     * 增加页面信息
     *
     * @param lstReferenceDto
     * @param result
     */
    private void addReferenceInfo(List<ReferenceDto> lstReferenceDto, List<Map<String, Object>> result) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 2);
        map.put("code", "002");
        map.put("name", "引用");
        map.put("type", "2");
        result.add(map);
        LevelProvider provider = new LevelProvider(SysCodeRule
                .createClient(new int[]{3, 6, 9}), "");
        if (lstReferenceDto != null && !lstReferenceDto.isEmpty()) {
            for (ReferenceDto dto : lstReferenceDto) {
                map = new HashMap<>();
                map.put("id", dto.getRefId());
                map.put("code", "002" + provider.getNextCode());
                map.put("name", dto.getRefName());
                map.put("type", "2");
                result.add(map);
            }
        }
    }
}
