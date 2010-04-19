package org.akaza.openclinica.service.crfdata;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.dao.hibernate.DynamicsItemFormMetadataDao;
import org.akaza.openclinica.dao.hibernate.DynamicsItemGroupMetadataDao;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.domain.crfdata.DynamicsItemFormMetadataBean;
import org.akaza.openclinica.domain.crfdata.DynamicsItemGroupMetadataBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.action.PropertyBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.service.rule.expression.ExpressionService;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class DynamicsMetadataService implements MetadataServiceInterface {

    // protected final java.util.logging.Logger logger = LoggerFactory.getLogger(getClass().getName());
    private final String ESCAPED_SEPERATOR = "\\.";
    private DynamicsItemFormMetadataDao dynamicsItemFormMetadataDao;
    private DynamicsItemGroupMetadataDao dynamicsItemGroupMetadataDao;
    DataSource ds;
    private EventCRFDAO eventCRFDAO;
    private ItemDataDAO itemDataDAO;
    private ItemDAO itemDAO;
    private ItemGroupDAO itemGroupDAO;
    private SectionDAO sectionDAO;
    // private CRFVersionDAO crfVersionDAO;
    private ItemFormMetadataDAO itemFormMetadataDAO;
    private ItemGroupMetadataDAO itemGroupMetadataDAO;
    private StudyEventDAO studyEventDAO;
    private EventDefinitionCRFDAO eventDefinitionCRFDAO;
    private ExpressionService expressionService;

    public DynamicsMetadataService(DataSource ds) {
        this.ds = ds;
    }

    public boolean hide(Object metadataBean, EventCRFBean eventCrfBean) {
        // TODO -- interesting problem, where is the SpringServletAccess object going to live now? tbh 03/2010
        ItemFormMetadataBean itemFormMetadataBean = (ItemFormMetadataBean) metadataBean;
        itemFormMetadataBean.setShowItem(false);
        // DynamicsItemFormMetadataDao dynamicsMetadataDao = (DynamicsItemFormMetadataDao) metadataDao;
        // DynamicsItemFormMetadataDao metadataDao = (DynamicsItemFormMetadataDao) SpringServletAccess.getApplicationContext(context).getBean("dynamicsItemFormMetadataDao");
        DynamicsItemFormMetadataBean dynamicsMetadataBean = new DynamicsItemFormMetadataBean(itemFormMetadataBean, eventCrfBean);
        dynamicsMetadataBean.setShowItem(false);
        getDynamicsItemFormMetadataDao().saveOrUpdate(dynamicsMetadataBean);
        return true;
    }

    public boolean isShown(Object metadataBean, EventCRFBean eventCrfBean) {
        ItemFormMetadataBean itemFormMetadataBean = (ItemFormMetadataBean) metadataBean;
        DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsItemFormMetadataBean(itemFormMetadataBean, eventCrfBean, null);
        if (dynamicsMetadataBean != null) {
            return dynamicsMetadataBean.isShowItem();
        } else {
            System.out.println("did not find a row in the db for " + itemFormMetadataBean.getId());
            return false;
        }
        // return false;
    }

    public boolean isShown(Integer itemId, EventCRFBean eventCrfBean) {
        // do we check against the database, or just against the object? prob against the db
        // ItemFormMetadataBean itemFormMetadataBean = (ItemFormMetadataBean) metadataBean;
        // return itemFormMetadataBean.isShowItem();
        ItemFormMetadataBean itemFormMetadataBean = getItemFormMetadataDAO().findByItemIdAndCRFVersionId(itemId, eventCrfBean.getCRFVersionId());
        DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsItemFormMetadataBean(itemFormMetadataBean, eventCrfBean, null);
        // DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsItemFormMetadataDao().findByMetadataBean(itemFormMetadataBean, eventCrfBean);
        if (dynamicsMetadataBean != null) {
            return dynamicsMetadataBean.isShowItem();
        } else {
            System.out.println("did not find a row in the db for " + itemFormMetadataBean.getId());
            return false;
        }
        // return false;
    }

    public boolean isShown(Integer itemId, EventCRFBean eventCrfBean, ItemDataBean itemDataBean) {
        // do we check against the database, or just against the object? prob against the db
        // ItemFormMetadataBean itemFormMetadataBean = (ItemFormMetadataBean) metadataBean;
        // return itemFormMetadataBean.isShowItem();
        ItemFormMetadataBean itemFormMetadataBean = getItemFormMetadataDAO().findByItemIdAndCRFVersionId(itemId, eventCrfBean.getCRFVersionId());
        DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsItemFormMetadataBean(itemFormMetadataBean, eventCrfBean, itemDataBean);
        // DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsItemFormMetadataDao().findByMetadataBean(itemFormMetadataBean, eventCrfBean);
        if (dynamicsMetadataBean != null) {
            return dynamicsMetadataBean.isShowItem();
        } else {
            // System.out.println("did not find a row in the db for (with IDB) " + itemFormMetadataBean.getId() + " idb id " + itemDataBean.getId());
            return false;
        }
        // return false;
    }

    public boolean isGroupShown(int metadataId, EventCRFBean eventCrfBean) throws OpenClinicaException {
        ItemGroupMetadataBean itemGroupMetadataBean = (ItemGroupMetadataBean) getItemGroupMetadataDAO().findByPK(metadataId);
        DynamicsItemGroupMetadataBean dynamicsMetadataBean = getDynamicsItemGroupMetadataBean(itemGroupMetadataBean, eventCrfBean);
        if (dynamicsMetadataBean != null) {
            return dynamicsMetadataBean.isShowGroup();
        } else {
            System.out.println("didnt find a group row in the db ");
            return false;
        }

    }

    public boolean isGroupShown(int metadataId, int eventCrfBeanId) throws OpenClinicaException {
        ItemGroupMetadataBean itemGroupMetadataBean = (ItemGroupMetadataBean) getItemGroupMetadataDAO().findByPK(metadataId);
        DynamicsItemGroupMetadataBean dynamicsMetadataBean = getDynamicsItemGroupMetadataBean(itemGroupMetadataBean, eventCrfBeanId);
        if (dynamicsMetadataBean != null) {
            return dynamicsMetadataBean.isShowGroup();
        } else {
            System.out.println("didnt find a group row in the db ");
            return false;
        }
    }

    /**
     * 
     * TODO: remove the @deprecated call. The reason it is there now is to accommodate the call being made from the DataEntryServlet
     * 
     * @param metadataBean
     * @param eventCrfBean
     * @param itemDataBean
     * @return DynamicsItemFormMetadataBean
     */
    private DynamicsItemFormMetadataBean getDynamicsItemFormMetadataBean(ItemFormMetadataBean metadataBean, EventCRFBean eventCrfBean, ItemDataBean itemDataBean) {
        ItemFormMetadataBean itemFormMetadataBean = metadataBean;
        DynamicsItemFormMetadataBean dynamicsMetadataBean = null;
        if (itemDataBean == null) {
            dynamicsMetadataBean = getDynamicsItemFormMetadataDao().findByMetadataBean(itemFormMetadataBean, eventCrfBean);
        } else {
            dynamicsMetadataBean = getDynamicsItemFormMetadataDao().findByMetadataBean(itemFormMetadataBean, eventCrfBean, itemDataBean);
        }

        return dynamicsMetadataBean;

    }

    private DynamicsItemGroupMetadataBean getDynamicsItemGroupMetadataBean(ItemGroupMetadataBean metadataBean, EventCRFBean eventCrfBean) {

        DynamicsItemGroupMetadataBean dynamicsMetadataBean = getDynamicsItemGroupMetadataDao().findByMetadataBean(metadataBean, eventCrfBean);
        System.out.println(" returning " + metadataBean.getId() + " " + metadataBean.getItemGroupId() + " " + eventCrfBean.getId());
        return dynamicsMetadataBean;

    }

    private DynamicsItemGroupMetadataBean getDynamicsItemGroupMetadataBean(ItemGroupMetadataBean metadataBean, int eventCrfBeanId) {

        DynamicsItemGroupMetadataBean dynamicsMetadataBean = null;
        dynamicsMetadataBean = getDynamicsItemGroupMetadataDao().findByMetadataBean(metadataBean, eventCrfBeanId);
        return dynamicsMetadataBean;

    }

    public boolean showItem(ItemFormMetadataBean metadataBean, EventCRFBean eventCrfBean, ItemDataBean itemDataBean) {
        ItemFormMetadataBean itemFormMetadataBean = metadataBean;
        itemFormMetadataBean.setShowItem(true);
        DynamicsItemFormMetadataBean dynamicsMetadataBean = new DynamicsItemFormMetadataBean(itemFormMetadataBean, eventCrfBean);
        dynamicsMetadataBean.setItemDataId(itemDataBean.getId());
        dynamicsMetadataBean.setShowItem(true);
        getDynamicsItemFormMetadataDao().saveOrUpdate(dynamicsMetadataBean);
        return true;
    }

    public boolean hideItem(ItemFormMetadataBean metadataBean, EventCRFBean eventCrfBean, ItemDataBean itemDataBean) {
        ItemFormMetadataBean itemFormMetadataBean = metadataBean;
        DynamicsItemFormMetadataBean dynamicsMetadataBean = new DynamicsItemFormMetadataBean(itemFormMetadataBean, eventCrfBean);
        dynamicsMetadataBean.setItemDataId(itemDataBean.getId());
        dynamicsMetadataBean.setShowItem(false);
        getDynamicsItemFormMetadataDao().saveOrUpdate(dynamicsMetadataBean);
        return true;
    }

    public boolean showGroup(ItemGroupMetadataBean metadataBean, EventCRFBean eventCrfBean) {

        ItemGroupMetadataBean itemGroupMetadataBean = metadataBean;
        itemGroupMetadataBean.setShowGroup(true);
        DynamicsItemGroupMetadataBean dynamicsMetadataBean = new DynamicsItemGroupMetadataBean(itemGroupMetadataBean, eventCrfBean);
        getDynamicsItemGroupMetadataDao().saveOrUpdate(dynamicsMetadataBean);
        return true;
    }

    public void show(Integer itemDataId, String[] oids) {
        ItemDataBean itemDataBean = (ItemDataBean) getItemDataDAO().findByPK(itemDataId);
        EventCRFBean eventCrfBean = (EventCRFBean) getEventCRFDAO().findByPK(itemDataBean.getEventCRFId());
        for (String oid : oids) {
            ItemOrItemGroupHolder itemOrItemGroup = getItemOrItemGroup(oid);
            // OID is an item
            if (itemOrItemGroup.getItemBean() != null) {
                ItemDataBean oidBasedItemData = getItemData(itemOrItemGroup.getItemBean(), eventCrfBean, itemDataBean.getOrdinal());
                ItemFormMetadataBean itemFormMetadataBean =
                    getItemFormMetadataDAO().findByItemIdAndCRFVersionId(itemOrItemGroup.getItemBean().getId(), eventCrfBean.getCRFVersionId());
                DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsItemFormMetadataBean(itemFormMetadataBean, eventCrfBean, oidBasedItemData);
                if (dynamicsMetadataBean == null) {
                    showItem(itemFormMetadataBean, eventCrfBean, oidBasedItemData);
                } else if (dynamicsMetadataBean != null && !dynamicsMetadataBean.isShowItem()) {
                    dynamicsMetadataBean.setShowItem(true);
                    getDynamicsItemFormMetadataDao().saveOrUpdate(dynamicsMetadataBean);
                }
            }
            // OID is a group
            else {
                System.out.println("found item group id 1 " + oid);
                ItemGroupBean itemGroupBean = itemOrItemGroup.getItemGroupBean();
                ArrayList sectionBeans = getSectionDAO().findAllByCRFVersionId(eventCrfBean.getCRFVersionId());
                for (int i = 0; i < sectionBeans.size(); i++) {
                    SectionBean sectionBean = (SectionBean) sectionBeans.get(i);
                    // System.out.println("found section " + sectionBean.getId());
                    List<ItemGroupMetadataBean> itemGroupMetadataBeans =
                        getItemGroupMetadataDAO().findMetaByGroupAndSection(itemGroupBean.getId(), eventCrfBean.getCRFVersionId(), sectionBean.getId());
                    for (ItemGroupMetadataBean itemGroupMetadataBean : itemGroupMetadataBeans) {
                        if (itemGroupMetadataBean.getItemGroupId() == itemGroupBean.getId()) {
                            // System.out.println("found item group id 2 " + oid);
                            DynamicsItemGroupMetadataBean dynamicsGroupBean = getDynamicsItemGroupMetadataBean(itemGroupMetadataBean, eventCrfBean);
                            if (dynamicsGroupBean == null) {
                                showGroup(itemGroupMetadataBean, eventCrfBean);
                            } else if (dynamicsGroupBean != null && !dynamicsGroupBean.isShowGroup()) {
                                dynamicsGroupBean.setShowGroup(true);
                                getDynamicsItemGroupMetadataDao().saveOrUpdate(dynamicsGroupBean);
                            }
                        }
                    }
                }
            }
        }
    }

    public void hide(Integer itemDataId, String[] oids) {
        ItemDataBean itemDataBean = (ItemDataBean) getItemDataDAO().findByPK(itemDataId);
        EventCRFBean eventCrfBean = (EventCRFBean) getEventCRFDAO().findByPK(itemDataBean.getEventCRFId());
        for (String oid : oids) {
            ItemOrItemGroupHolder itemOrItemGroup = getItemOrItemGroup(oid);
            // OID is an item
            if (itemOrItemGroup.getItemBean() != null) {
                ItemDataBean oidBasedItemData = getItemData(itemOrItemGroup.getItemBean(), eventCrfBean, itemDataBean.getOrdinal());
                ItemFormMetadataBean itemFormMetadataBean =
                    getItemFormMetadataDAO().findByItemIdAndCRFVersionId(itemOrItemGroup.getItemBean().getId(), eventCrfBean.getCRFVersionId());
                DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsItemFormMetadataBean(itemFormMetadataBean, eventCrfBean, oidBasedItemData);
                if (dynamicsMetadataBean == null && oidBasedItemData.getValue().equals("")) {
                    showItem(itemFormMetadataBean, eventCrfBean, oidBasedItemData);
                } else if (dynamicsMetadataBean != null && dynamicsMetadataBean.isShowItem() && oidBasedItemData.getValue().equals("")) {
                    dynamicsMetadataBean.setShowItem(false);
                    getDynamicsItemFormMetadataDao().saveOrUpdate(dynamicsMetadataBean);
                }
            }
            // OID is a group
            else {
                // ItemGroupBean itemGroupBean = itemOrItemGroup.getItemGroupBean();
            }
        }
    }

    private Boolean isGroupRepeating(ItemGroupMetadataBean itemGroupMetadataBean) {
        return itemGroupMetadataBean.getRepeatNum() > 1 || itemGroupMetadataBean.getRepeatMax() > 1;
    }

    /**
     * 
     * 
     * @param itemDataBeanA
     * @param eventCrfBeanA
     * @param itemGroupMetadataBeanA
     * @param itemBeanB
     * @param itemGroupBeanB
     * @param itemGroupMetadataBeanB
     * @param ub
     * @param value
     */
    private void oneToMany(ItemDataBean itemDataBeanA, EventCRFBean eventCrfBeanA, ItemGroupMetadataBean itemGroupMetadataBeanA, ItemBean itemBeanB,
            ItemGroupBean itemGroupBeanB, ItemGroupMetadataBean itemGroupMetadataBeanB, EventCRFBean eventCrfBeanB, UserAccountBean ub, String value) {

        Integer size = getItemDataDAO().getGroupSize(itemBeanB.getId(), eventCrfBeanB.getId());
        int maxOrdinal = getItemDataDAO().getMaxOrdinalForGroupByItemAndEventCrf(itemBeanB, eventCrfBeanB);
        if (size > 0 || maxOrdinal > 0) {
            List<ItemDataBean> itemDataBeans = getItemDataDAO().findAllByEventCRFIdAndItemId(eventCrfBeanB.getId(), itemBeanB.getId());
            for (ItemDataBean oidBasedItemData : itemDataBeans) {
                oidBasedItemData.setValue(value);
                getItemDataDAO().updateValue(oidBasedItemData, "yyyy-MM-dd");
            }
        } else {
            List<ItemBean> items = getItemDAO().findAllItemsByGroupId(itemGroupBeanB.getId(), eventCrfBeanB.getCRFVersionId());
            for (int ordinal = 1 + maxOrdinal; ordinal <= itemGroupMetadataBeanB.getRepeatNum() + maxOrdinal; ordinal++) {
                for (ItemBean itemBeanX : items) {
                    ItemDataBean oidBasedItemData = getItemData(itemBeanX, eventCrfBeanB, ordinal);
                    if (oidBasedItemData.getId() == 0) {
                        oidBasedItemData = createItemData(oidBasedItemData, itemBeanX, ordinal, eventCrfBeanB, ub);
                    }
                    if (itemBeanX.getId() == itemBeanB.getId()) {
                        oidBasedItemData.setValue(value);
                        getItemDataDAO().updateValue(oidBasedItemData, "yyyy-MM-dd");
                    }
                }
            }
        }
    }

    private void oneToIndexedMany(ItemDataBean itemDataBeanA, EventCRFBean eventCrfBeanA, ItemGroupMetadataBean itemGroupMetadataBeanA, ItemBean itemBeanB,
            ItemGroupBean itemGroupBeanB, ItemGroupMetadataBean itemGroupMetadataBeanB, EventCRFBean eventCrfBeanB, UserAccountBean ub, String value, int index) {

        int size = getItemDataDAO().getGroupSize(itemBeanB.getId(), eventCrfBeanB.getId());
        int maxOrdinal = getItemDataDAO().getMaxOrdinalForGroupByItemAndEventCrf(itemBeanB, eventCrfBeanB);
        if (size > 0 && size >= index) {
            List<ItemDataBean> itemDataBeans = getItemDataDAO().findAllByEventCRFIdAndItemId(eventCrfBeanB.getId(), itemBeanB.getId());
            ItemDataBean oidBasedItemData = itemDataBeans.get(index - 1);
            oidBasedItemData.setValue(value);
            getItemDataDAO().updateValue(oidBasedItemData, "yyyy-MM-dd");
        } else {
            List<ItemBean> items = getItemDAO().findAllItemsByGroupId(itemGroupBeanB.getId(), eventCrfBeanB.getCRFVersionId());
            int number =
                itemGroupMetadataBeanB.getRepeatNum() > index ? itemGroupMetadataBeanB.getRepeatNum() : index <= itemGroupMetadataBeanB.getRepeatMax() ? index
                    : 0;
            for (int ordinal = 1 + maxOrdinal; ordinal <= number + maxOrdinal - size; ordinal++) {
                for (ItemBean itemBeanX : items) {
                    ItemDataBean oidBasedItemData = getItemData(itemBeanX, eventCrfBeanB, ordinal);
                    if (oidBasedItemData.getId() == 0) {
                        oidBasedItemData = createItemData(oidBasedItemData, itemBeanX, ordinal, eventCrfBeanB, ub);
                    }
                }
            }
            List<ItemDataBean> itemDataBeans = getItemDataDAO().findAllByEventCRFIdAndItemId(eventCrfBeanB.getId(), itemBeanB.getId());
            ItemDataBean oidBasedItemData = itemDataBeans.get(index - 1);
            oidBasedItemData.setValue(value);
            getItemDataDAO().updateValue(oidBasedItemData, "yyyy-MM-dd");
        }
    }

    private void oneToOne(ItemDataBean itemDataBeanA, EventCRFBean eventCrfBeanA, ItemGroupMetadataBean itemGroupMetadataBeanA, ItemBean itemBeanB,
            ItemGroupMetadataBean itemGroupMetadataBeanB, EventCRFBean eventCrfBeanB, UserAccountBean ub, Integer ordinal, String value) {
        ordinal = ordinal == null ? 1 : ordinal;
        itemGroupMetadataBeanB.getRepeatNum();
        ItemDataBean oidBasedItemData = getItemData(itemBeanB, eventCrfBeanB, ordinal);
        if (oidBasedItemData.getId() == 0) {
            oidBasedItemData = createItemData(oidBasedItemData, itemBeanB, ordinal, eventCrfBeanB, ub);
        }
        oidBasedItemData.setValue(value);
        getItemDataDAO().updateValue(oidBasedItemData, "yyyy-MM-dd");

    }

    private ItemDataBean createItemData(ItemDataBean oidBasedItemData, ItemBean itemBeanB, int ordinal, EventCRFBean eventCrfBeanA, UserAccountBean ub) {
        oidBasedItemData.setItemId(itemBeanB.getId());
        oidBasedItemData.setEventCRFId(eventCrfBeanA.getId());
        oidBasedItemData.setStatus(Status.AVAILABLE);
        oidBasedItemData.setOwner(ub);
        oidBasedItemData.setOrdinal(ordinal);
        oidBasedItemData = (ItemDataBean) getItemDataDAO().create(oidBasedItemData);
        return oidBasedItemData;
    }

    public void insert(Integer itemDataId, List<PropertyBean> properties, UserAccountBean ub, RuleSetBean ruleSet) {
        ItemDataBean itemDataBeanA = (ItemDataBean) getItemDataDAO().findByPK(itemDataId);
        EventCRFBean eventCrfBeanA = (EventCRFBean) getEventCRFDAO().findByPK(itemDataBeanA.getEventCRFId());
        StudyEventBean studyEventBeanA = (StudyEventBean) getStudyEventDAO().findByPK(eventCrfBeanA.getStudyEventId());
        ItemGroupMetadataBean itemGroupMetadataBeanA =
            (ItemGroupMetadataBean) getItemGroupMetadataDAO().findByItemAndCrfVersion(itemDataBeanA.getItemId(), eventCrfBeanA.getCRFVersionId());
        Boolean isGroupARepeating = isGroupRepeating(itemGroupMetadataBeanA);
        String itemGroupAOrdinal = getExpressionService().getGroupOrdninalCurated(ruleSet.getTarget().getValue());

        for (PropertyBean propertyBean : properties) {
            String expression = getExpressionService().constructFullExpressionIfPartialProvided(propertyBean.getOid(), ruleSet.getTarget().getValue());
            ItemBean itemBeanB = getExpressionService().getItemBeanFromExpression(expression);
            ItemGroupBean itemGroupBeanB = getExpressionService().getItemGroupExpression(expression);
            ItemGroupMetadataBean itemGroupMetadataBeanB = null;
            Boolean isGroupBRepeating = null;
            String itemGroupBOrdinal = null;
            EventCRFBean eventCrfBeanB = null;
            // Does item belong to same CRF version as target
            Boolean isItemInSameForm =
                getItemFormMetadataDAO().findByItemIdAndCRFVersionId(itemBeanB.getId(), eventCrfBeanA.getCRFVersionId()).getId() != 0 ? true : false;
            if (!isItemInSameForm) {
                List<EventCRFBean> eventCrfs =
                    getEventCRFDAO().findAllByStudyEventAndCrfOrCrfVersionOid(studyEventBeanA, getExpressionService().getCrfOid(expression));
                if (eventCrfs.size() == 0) {
                    CRFVersionBean crfVersion = getExpressionService().getCRFVersionFromExpression(expression);
                    CRFBean crf = getExpressionService().getCRFFromExpression(expression);
                    int crfVersionId = 0;
                    EventDefinitionCRFBean eventDefinitionCRFBean =
                        getEventDefinitionCRfDAO().findByStudyEventDefinitionIdAndCRFId(studyEventBeanA.getStudyEventDefinitionId(), crf.getId());
                    if (eventDefinitionCRFBean.getId() != 0) {
                        crfVersionId = crfVersion != null ? crfVersion.getId() : eventDefinitionCRFBean.getDefaultVersionId();

                    }
                    // Create new event crf
                    eventCrfBeanB = eventCrfBeanA;
                    eventCrfBeanB.setId(0);
                    eventCrfBeanB.setCRFVersionId(crfVersionId);
                    eventCrfBeanB = (EventCRFBean) getEventCRFDAO().create(eventCrfBeanB);

                } else {
                    eventCrfBeanB = eventCrfs.get(0);
                }
            }
            if (isItemInSameForm) {
                eventCrfBeanB = eventCrfBeanA;
            }

            itemGroupMetadataBeanB =
                (ItemGroupMetadataBean) getItemGroupMetadataDAO().findByItemAndCrfVersion(itemBeanB.getId(), eventCrfBeanB.getCRFVersionId());
            isGroupBRepeating = isGroupRepeating(itemGroupMetadataBeanB);
            itemGroupBOrdinal = getExpressionService().getGroupOrdninalCurated(expression);

            // If A and B are both non repeating groups
            if (!isGroupARepeating && !isGroupBRepeating) {
                oneToOne(itemDataBeanA, eventCrfBeanA, itemGroupMetadataBeanA, itemBeanB, itemGroupMetadataBeanB, eventCrfBeanB, ub, 1, propertyBean.getValue());
            }
            // If A is not repeating group & B is a repeating group with no index selected
            if (!isGroupARepeating && isGroupBRepeating && itemGroupBOrdinal.equals("")) {
                oneToMany(itemDataBeanA, eventCrfBeanA, itemGroupMetadataBeanA, itemBeanB, itemGroupBeanB, itemGroupMetadataBeanB, eventCrfBeanB, ub,
                        propertyBean.getValue());
            }
            // If A is not repeating group & B is a repeating group with index selected
            if (!isGroupARepeating && isGroupBRepeating && !itemGroupBOrdinal.equals("")) {
                oneToIndexedMany(itemDataBeanA, eventCrfBeanA, itemGroupMetadataBeanA, itemBeanB, itemGroupBeanB, itemGroupMetadataBeanB, eventCrfBeanB, ub,
                        propertyBean.getValue(), Integer.valueOf(itemGroupBOrdinal));
            }
            // If A is repeating group with index & B is a repeating group with index selected
            if (isGroupARepeating && isGroupBRepeating && !itemGroupBOrdinal.equals("")) {
                oneToIndexedMany(itemDataBeanA, eventCrfBeanA, itemGroupMetadataBeanA, itemBeanB, itemGroupBeanB, itemGroupMetadataBeanB, eventCrfBeanB, ub,
                        propertyBean.getValue(), Integer.valueOf(itemGroupBOrdinal));
            }
            // If A is repeating group with index & B is a repeating group with no index selected
            if (isGroupARepeating && isGroupBRepeating && itemGroupBOrdinal.equals("")) {
                oneToIndexedMany(itemDataBeanA, eventCrfBeanA, itemGroupMetadataBeanA, itemBeanB, itemGroupBeanB, itemGroupMetadataBeanB, eventCrfBeanB, ub,
                        propertyBean.getValue(), Integer.valueOf(itemGroupAOrdinal));
            }

        }
    }

    private ItemDataBean getItemData(ItemBean itemBean, EventCRFBean eventCrfBean, Integer ordinal) {
        return getItemDataDAO().findByItemIdAndEventCRFIdAndOrdinal(itemBean.getId(), eventCrfBean.getId(), ordinal);

    }

    private ItemOrItemGroupHolder getItemOrItemGroup(String oid) {

        String[] theOid = oid.split(ESCAPED_SEPERATOR);
        if (theOid.length == 2) {
            ItemGroupBean itemGroup = getItemGroupDAO().findByOid(theOid[0].trim());
            if (itemGroup != null) {
                ItemBean item = getItemDAO().findItemByGroupIdandItemOid(itemGroup.getId(), theOid[1].trim());
                if (item != null) {
                    System.out.println("returning two non nulls");
                    return new ItemOrItemGroupHolder(item, itemGroup);
                }
            }
        }
        if (theOid.length == 1) {
            ItemGroupBean itemGroup = getItemGroupDAO().findByOid(oid.trim());
            if (itemGroup != null) {
                System.out.println("returning item group not null");
                return new ItemOrItemGroupHolder(null, itemGroup);
            }

            List<ItemBean> items = getItemDAO().findByOid(oid.trim());
            ItemBean item = items.size() > 0 ? items.get(0) : null;
            if (item != null) {
                System.out.println("returning item not null");
                return new ItemOrItemGroupHolder(item, null);
            }
        }

        return new ItemOrItemGroupHolder(null, null);
    }

    public DynamicsItemFormMetadataDao getDynamicsItemFormMetadataDao() {
        return dynamicsItemFormMetadataDao;
    }

    public DynamicsItemGroupMetadataDao getDynamicsItemGroupMetadataDao() {
        return dynamicsItemGroupMetadataDao;
    }

    public void setDynamicsItemGroupMetadataDao(DynamicsItemGroupMetadataDao dynamicsItemGroupMetadataDao) {
        this.dynamicsItemGroupMetadataDao = dynamicsItemGroupMetadataDao;
    }

    public void setDynamicsItemFormMetadataDao(DynamicsItemFormMetadataDao dynamicsItemFormMetadataDao) {
        this.dynamicsItemFormMetadataDao = dynamicsItemFormMetadataDao;
    }

    private EventCRFDAO getEventCRFDAO() {
        eventCRFDAO = this.eventCRFDAO != null ? eventCRFDAO : new EventCRFDAO(ds);
        return eventCRFDAO;
    }

    private ItemDataDAO getItemDataDAO() {
        itemDataDAO = this.itemDataDAO != null ? itemDataDAO : new ItemDataDAO(ds);
        return itemDataDAO;
    }

    private ItemDAO getItemDAO() {
        itemDAO = this.itemDAO != null ? itemDAO : new ItemDAO(ds);
        return itemDAO;
    }

    private ItemGroupDAO getItemGroupDAO() {
        itemGroupDAO = this.itemGroupDAO != null ? itemGroupDAO : new ItemGroupDAO(ds);
        return itemGroupDAO;
    }

    private SectionDAO getSectionDAO() {
        sectionDAO = this.sectionDAO != null ? sectionDAO : new SectionDAO(ds);
        return sectionDAO;
    }

    private ItemFormMetadataDAO getItemFormMetadataDAO() {
        itemFormMetadataDAO = this.itemFormMetadataDAO != null ? itemFormMetadataDAO : new ItemFormMetadataDAO(ds);
        return itemFormMetadataDAO;
    }

    private ItemGroupMetadataDAO getItemGroupMetadataDAO() {
        itemGroupMetadataDAO = this.itemGroupMetadataDAO != null ? itemGroupMetadataDAO : new ItemGroupMetadataDAO(ds);
        return itemGroupMetadataDAO;
    }

    public StudyEventDAO getStudyEventDAO() {
        studyEventDAO = this.studyEventDAO != null ? studyEventDAO : new StudyEventDAO(ds);
        return studyEventDAO;
    }

    public EventDefinitionCRFDAO getEventDefinitionCRfDAO() {
        eventDefinitionCRFDAO = this.eventDefinitionCRFDAO != null ? eventDefinitionCRFDAO : new EventDefinitionCRFDAO(ds);
        return eventDefinitionCRFDAO;
    }

    public ExpressionService getExpressionService() {
        return expressionService;
    }

    public void setExpressionService(ExpressionService expressionService) {
        this.expressionService = expressionService;
    }

    class ItemOrItemGroupHolder {

        ItemBean itemBean;
        ItemGroupBean itemGroupBean;

        public ItemOrItemGroupHolder(ItemBean itemBean, ItemGroupBean itemGroupBean) {
            this.itemBean = itemBean;
            this.itemGroupBean = itemGroupBean;
        }

        public ItemBean getItemBean() {
            return itemBean;
        }

        public void setItemBean(ItemBean itemBean) {
            this.itemBean = itemBean;
        }

        public ItemGroupBean getItemGroupBean() {
            return itemGroupBean;
        }

        public void setItemGroupBean(ItemGroupBean itemGroupBean) {
            this.itemGroupBean = itemGroupBean;
        }

    }

}
