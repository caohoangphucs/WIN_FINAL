package com.example.winfinal.service;

import com.example.winfinal.dao.LookupDAOs.*;
import com.example.winfinal.entity.lookup.*;
import com.example.winfinal.entity.master.*;
import java.util.List;

public class LookupService {
    private final RoleDAO roleDAO = new RoleDAO();
    private final LotStatusDAO lotStatusDAO = new LotStatusDAO();
    private final SupplyCategoryDAO supplyCategoryDAO = new SupplyCategoryDAO();
    private final CropCategoryDAO cropCategoryDAO = new CropCategoryDAO();
    private final SeasonDAO seasonDAO = new SeasonDAO();
    private final ActivityTypeDAO activityTypeDAO = new ActivityTypeDAO();
    private final QualityGradeDAO qualityGradeDAO = new QualityGradeDAO();
    private final SeverityLevelDAO severityLevelDAO = new SeverityLevelDAO();

    public List<Role> getAllRoles() { return roleDAO.findAll(); }
    public List<LotStatus> getAllLotStatuses() { return lotStatusDAO.findAll(); }
    public List<SupplyCategory> getAllSupplyCategories() { return supplyCategoryDAO.findAll(); }
    public List<CropCategory> getAllCropCategories() { return cropCategoryDAO.findAll(); }
    public List<Season> getAllSeasons() { return seasonDAO.findAll(); }
    public List<ActivityType> getAllActivityTypes() { return activityTypeDAO.findAll(); }
    public List<QualityGrade> getAllQualityGrades() { return qualityGradeDAO.findAll(); }
    public List<SeverityLevel> getAllSeverityLevels() { return severityLevelDAO.findAll(); }
}
