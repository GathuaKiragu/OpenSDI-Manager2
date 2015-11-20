package it.geosolutions.opensdi.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "CropDescriptor")
@Table(name = "cropdescriptor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "cropdescriptor")
@XmlRootElement(name = "CropDescriptor")
public class CropDescriptor implements Serializable{

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    @Id
	String id;
    
    @Column(updatable=true,nullable=false)
	String label;
    
    @Column(updatable=true,nullable=false)
	String seasons; // ???
    
    @Column(updatable=true,nullable=false, columnDefinition="varchar(20) default '000_tons'")
    String prod_default_unit;
    
    @Column(updatable=true,nullable=false, columnDefinition="varchar(20) default '000_ha'")
    String area_default_unit;
    
    @Column(updatable=true,nullable=false, columnDefinition="varchar(20) default 'kg_ha'")
    String yield_default_unit;

    public CropDescriptor() {
    }

    public CropDescriptor(String id, String label) {
        this.id = id;
        this.label = label;
    }

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public String getProd_default_unit() {
		return prod_default_unit;
	}

	public void setProd_default_unit(String prod_default_unit) {
		this.prod_default_unit = prod_default_unit;
	}

	public String getArea_default_unit() {
		return area_default_unit;
	}

	public void setArea_default_unit(String area_default_unit) {
		this.area_default_unit = area_default_unit;
	}

	public String getYield_default_unit() {
		return yield_default_unit;
	}

	public void setYield_default_unit(String yield_default_unit) {
		this.yield_default_unit = yield_default_unit;
	}

    @Override
    public String toString() {
        return "CropDescriptor[id=" + id + ", label=" + label + ", seasons=" + seasons + ']';
    }
}