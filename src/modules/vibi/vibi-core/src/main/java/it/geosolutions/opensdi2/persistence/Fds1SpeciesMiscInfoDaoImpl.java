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
package it.geosolutions.opensdi2.persistence;

import org.springframework.transaction.annotation.Transactional;

@Transactional(value = "opensdiTransactionManager")
public class Fds1SpeciesMiscInfoDaoImpl extends BaseDao<Fds1SpeciesMiscInfo, String>
        implements Fds1SpeciesMiscInfoDao {

    @Override
    public void persist(Fds1SpeciesMiscInfo... entities) {
        super.persist(entities);
    }

    @Override
    public Fds1SpeciesMiscInfo merge(Fds1SpeciesMiscInfo entity) {
        return super.merge(entity);
    }

    @Override
    public boolean remove(Fds1SpeciesMiscInfo entity) {
        return super.remove(entity);
    }

    @Override
    public boolean removeById(String id) {
        return super.removeById(id);
    }

    private static String[] PKNames = {};

    public String[] getPKNames() {
        return PKNames;
    }

    @Override
    public Class<Fds1SpeciesMiscInfo> getEntityType() {
        return Fds1SpeciesMiscInfo.class;
    }
}