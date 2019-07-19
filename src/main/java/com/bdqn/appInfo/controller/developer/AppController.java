package com.bdqn.appInfo.controller.developer;

import com.bdqn.appInfo.exception.BusinessExcpetion;
import com.bdqn.appInfo.pojo.Category;
import com.bdqn.appInfo.pojo.Dev_User;
import com.bdqn.appInfo.pojo.Dictionary;
import com.bdqn.appInfo.service.AppInfoService;
import com.bdqn.appInfo.service.CategoryService;
import com.bdqn.appInfo.service.DictionaryService;
import com.bdqn.appInfo.utils.Constants;
import com.bdqn.appInfo.utils.PageSupport;
import com.sun.deploy.ui.AppInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @ClassName: AppController
 * @Description: APP相关控制器
 * @Author: xyf
 * @Date 2019/7/19 15:01
 */
@Controller
@RequestMapping(value = "/app")
public class AppController {

    private Logger logger = Logger.getLogger(AppController.class);

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private CategoryService categoryService;


    /**
     * @Description: 获取APP分页信息
     * @param: [model, session, querySoftwareName, _queryStatus, _queryCategoryLevel1, _queryCategoryLevel2, _queryCategoryLevel3, _queryFlatformId, pageIndex]
     * @return: java.lang.String
     * @Date: 2019/07/19 16:38
     */
    @RequestMapping(value = "/list")
    public String getAppInfoList(Model model, HttpSession session,
                                 @RequestParam(value = "querySoftwareName", required = false) String querySoftwareName,
                                 @RequestParam(value = "queryStatus", required = false) String _queryStatus,
                                 @RequestParam(value = "queryCategoryLevel1", required = false) String _queryCategoryLevel1,
                                 @RequestParam(value = "queryCategoryLevel2", required = false) String _queryCategoryLevel2,
                                 @RequestParam(value = "queryCategoryLevel3", required = false) String _queryCategoryLevel3,
                                 @RequestParam(value = "queryFlatformId", required = false) String _queryFlatformId,
                                 @RequestParam(value = "pageIndex", required = false) String pageIndex) throws BusinessExcpetion {
        logger.info("getAppInfoList -- > querySoftwareName: " + querySoftwareName);
        logger.info("getAppInfoList -- > queryStatus: " + _queryStatus);
        logger.info("getAppInfoList -- > queryCategoryLevel1: " + _queryCategoryLevel1);
        logger.info("getAppInfoList -- > queryCategoryLevel2: " + _queryCategoryLevel2);
        logger.info("getAppInfoList -- > queryCategoryLevel3: " + _queryCategoryLevel3);
        logger.info("getAppInfoList -- > queryFlatformId: " + _queryFlatformId);
        logger.info("getAppInfoList -- > pageIndex: " + pageIndex);
        Long devId = ((Dev_User) session.getAttribute(Constants.DEVUSER_SESSION)).getId();
        List<AppInfo> appInfoList = null;
        List<Dictionary> statusList = null;
        List<Dictionary> flatFormList = null;
        List<Category> categoryLevel1List = null;//列出一级分类列表，注：二级和三级分类列表通过异步ajax获取
        List<Category> categoryLevel2List = null;//列出一级分类列表，注：二级和三级分类列表通过异步ajax获取
        List<Category> categoryLevel3List = null;//列出一级分类列表，注：二级和三级分类列表通过异步ajax获取
        //页面容量
        int pageSize = Constants.PAGESIZE;
        //当前页码
        Integer currentPageNo = 1;
        if (pageIndex != null) {
            currentPageNo = Integer.valueOf(pageIndex);
        }
        Integer queryStatus = null;
        if (_queryStatus != null && !_queryStatus.equals("")) {
            queryStatus = Integer.parseInt(_queryStatus);
        }
        Integer queryCategoryLevel1 = null;
        if (_queryCategoryLevel1 != null && !_queryCategoryLevel1.equals("")) {
            queryCategoryLevel1 = Integer.parseInt(_queryCategoryLevel1);
        }
        Integer queryCategoryLevel2 = null;
        if (_queryCategoryLevel2 != null && !_queryCategoryLevel2.equals("")) {
            queryCategoryLevel2 = Integer.parseInt(_queryCategoryLevel2);
        }
        Integer queryCategoryLevel3 = null;
        if (_queryCategoryLevel3 != null && !_queryCategoryLevel3.equals("")) {
            queryCategoryLevel3 = Integer.parseInt(_queryCategoryLevel3);
        }
        Integer queryFlatformId = null;
        if (_queryFlatformId != null && !_queryFlatformId.equals("")) {
            queryFlatformId = Integer.parseInt(_queryFlatformId);
        }
        //总数量（表）
        int totalCount = 0;
        totalCount = appInfoService.getAppInfoCount(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId);
        //总页数
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        int totalPageCount = pages.getTotalPageCount();
        //控制首页和尾页
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }
        appInfoList = appInfoService.getAppInfoList(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId, currentPageNo, pageSize);
        statusList=this.getDataDictionaryList("APP_STATUS");
        flatFormList = this.getDataDictionaryList("APP_FLATFORM");
        categoryLevel1List =categoryService.getAppCategoryListByParentId(null);
        return null;

    }


    /**
     * @Description: 获取类型信息
     * @param: [typeCode]
     * @return: java.util.List<com.bdqn.appInfo.pojo.Dictionary>
     * @Date: 2019/07/19 16:46
     */
    public List<Dictionary> getDataDictionaryList(String typeCode) throws BusinessExcpetion {
        List<Dictionary> dictionaryList = null;
        dictionaryList=dictionaryService.getDataDictionaryList(typeCode);
        if (dictionaryList==null){
            return null;
        }
        return dictionaryList;
    }
}
