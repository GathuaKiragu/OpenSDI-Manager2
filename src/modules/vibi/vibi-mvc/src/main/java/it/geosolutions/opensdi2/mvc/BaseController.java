/*
 *  Copyright (C) 2016 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.opensdi2.mvc;

import com.googlecode.genericdao.search.SearchResult;
import it.geosolutions.opensdi2.old.dto.CRUDResponseWrapper;
import it.geosolutions.opensdi2.service.BaseService;
import it.geosolutions.opensdi2.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.List;
import java.util.UUID;

public abstract class BaseController<T, K extends Serializable> extends BaseFileManager {

    private static File temporaryFolder = new File(System.getProperty("java.io.tmpdir"));

    @Autowired
    SecurityService securityService;

    protected abstract BaseService<T, K> getBaseService();

    @RequestMapping(value = "", method = RequestMethod.GET)
    public
    @ResponseBody
    CRUDResponseWrapper<T> list(@RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String filters,
                                @RequestParam(required = false) String ordering,
                                @RequestParam(required = false, defaultValue = "50") Integer maxResults,
                                @RequestParam(required = false, defaultValue = "-1") Integer firstResult,
                                @RequestParam(required = false, defaultValue = "-1") Integer page) {
        SearchResult searchResult = getBaseService().getAll(keyword, filters, ordering, maxResults, firstResult, page);
        CRUDResponseWrapper<T> responseWrapper = new CRUDResponseWrapper<T>();
        responseWrapper.setCount(searchResult.getResult().size());
        responseWrapper.setTotalCount(searchResult.getTotalCount());
        responseWrapper.setData(searchResult.getResult());
        securityService.validate("crud", "read", getBaseService().getEntityName(), "json", searchResult.getResult().size());
        return responseWrapper;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public
    @ResponseBody
    void create(@RequestBody T entity) {
        securityService.validate("crud", "create", getBaseService().getEntityName(), null, null);
        getBaseService().persist(entity);
        getBaseService().refreshCalculations();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    void update(@PathVariable(value = "id") K id, @RequestBody T entity) {
        securityService.validate("crud", "update", getBaseService().getEntityName(), null, null);
        getBaseService().merge(decode(id), entity);
        getBaseService().refreshCalculations();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    void delete(@PathVariable(value = "id") K id) {
        securityService.validate("crud", "delete", getBaseService().getEntityName(), null, null);
        getBaseService().delete(decode(id));
        getBaseService().refreshCalculations();
    }

    @RequestMapping(value = "export", method = {RequestMethod.POST, RequestMethod.GET})
    public void export(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String filters,
                       @RequestParam(required = false) String ordering,
                       @RequestParam(required = false, defaultValue = "50") Integer maxResults,
                       @RequestParam(required = false, defaultValue = "-1") Integer firstResult,
                       @RequestParam(required = false, defaultValue = "-1") Integer page,
                       @RequestParam(required = false, defaultValue = "csv") String format,
                       @RequestParam(required = false) String mappings,
                       HttpServletResponse response) {
        SearchResult searchResult = getBaseService().getAll(keyword, filters, ordering, maxResults, firstResult, page);
        securityService.validate("download", "export", getBaseService().getEntityName(), format, searchResult.getResult().size());
        File folder = new File(temporaryFolder, UUID.randomUUID().toString());
        super.newFolder("", folder.getPath());
        try {
            String file = handleExport(format, folder, searchResult.getResult(), mappings);
            super.downloadFile("", folder.getAbsolutePath(), file, response);
        } finally {
            super.deleteFolder("", folder.getAbsolutePath(), "");
        }
    }

    private K decode(K value) {
        if (value instanceof String) {
            try {
                return (K) URLDecoder.decode((String) value, "UTF-8");
            } catch (Exception exception) {
                throw new RuntimeException(String.format("Error encoding string '%s'.", value), exception);
            }
        } else {
            return value;
        }
    }

    private String handleExport(String format, File folder, List<Object> entities, String propertiesMappings) {
        if (format.equalsIgnoreCase("csv")) {
            return handleCsv(folder, entities, propertiesMappings);
        }
        if (format.equalsIgnoreCase("excel")) {
            return handleExcel(folder, entities, propertiesMappings);
        }
        throw new RuntimeException(String.format(
                "Invalid export format '%s', available ones are: ['%s', '%s'].", format, "csv", "excel"));
    }

    private String handleCsv(File folder, List<Object> entities, String propertiesMappings) {
        String fileName = String.format("%s-%s.csv", System.currentTimeMillis(), getBaseService().getEntityName());
        File file = new File(folder, fileName);
        getBaseService().writeEntitiesToCsv(file, entities, propertiesMappings);
        return fileName;
    }

    private String handleExcel(File folder, List<Object> entities, String propertiesMappings) {
        String fileName = String.format("%s-%s.xlsx", System.currentTimeMillis(), getBaseService().getEntityName());
        File file = new File(folder, fileName);
        getBaseService().writeEntitiesToExcel(file, entities, propertiesMappings);
        return fileName;
    }
}