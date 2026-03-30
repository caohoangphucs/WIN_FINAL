package com.example.winfinal.dao;
import com.example.winfinal.entity.lookup.*;
import com.example.winfinal.entity.master.*;

public class LookupDAOs {
    public static class RoleDAO extends BaseDAO<Role> { public RoleDAO() { super(Role.class); } }
    public static class LotStatusDAO extends BaseDAO<LotStatus> { public LotStatusDAO() { super(LotStatus.class); } }
    public static class ActivityTypeDAO extends BaseDAO<ActivityType> { public ActivityTypeDAO() { super(ActivityType.class); } }
    public static class QualityGradeDAO extends BaseDAO<QualityGrade> { public QualityGradeDAO() { super(QualityGrade.class); } }
    public static class SeverityLevelDAO extends BaseDAO<SeverityLevel> { public SeverityLevelDAO() { super(SeverityLevel.class); } }
    public static class SupplyCategoryDAO extends BaseDAO<SupplyCategory> { public SupplyCategoryDAO() { super(SupplyCategory.class); } }
    public static class CropCategoryDAO extends BaseDAO<CropCategory> { public CropCategoryDAO() { super(CropCategory.class); } }
    public static class SeasonDAO extends BaseDAO<Season> { public SeasonDAO() { super(Season.class); } }
}
