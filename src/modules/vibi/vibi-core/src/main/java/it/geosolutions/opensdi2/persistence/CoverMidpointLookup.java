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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity(name = "cover_midpoint_lookup")
@Table(name = "cover_midpoint_lookup")
@XmlRootElement(name = "cover_midpoint_lookup")
public class CoverMidpointLookup {

    @Id
    @Column(name = "cover_code")
    private Integer coverCode;

    @Column(name = "midpoint")
    private Double midPoint;

    public Integer getCoverCode() {
        return coverCode;
    }

    public void setCoverCode(Integer coverCode) {
        this.coverCode = coverCode;
    }

    public Double getMidPoint() {
        return midPoint;
    }

    public void setMidPoint(Double midPoint) {
        this.midPoint = midPoint;
    }
}