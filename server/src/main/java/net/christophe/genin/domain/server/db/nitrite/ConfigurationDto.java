package net.christophe.genin.domain.server.db.nitrite;

import org.dizitart.no2.objects.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ConfigurationDto implements Serializable {

    @Id
    private long confId = 0L;


    private List<String> javaFilters = new ArrayList<>();
    private List<String> npmFilters = new ArrayList<>();
    private Boolean activateElasticSearch = false;
    private String esHost;
    private Integer esPort;


    public long getConfId() {
        return confId;
    }

    public void setConfId(long confId) {
        this.confId = confId;
    }

    public List<String> getJavaFilters() {
        return javaFilters;
    }

    public Boolean getActivateElasticSearch() {
        return activateElasticSearch;
    }

    public void setActivateElasticSearch(Boolean activateElasticSearch) {
        this.activateElasticSearch = activateElasticSearch;
    }

    public String getEsHost() {
        return esHost;
    }

    public void setEsHost(String esHost) {
        this.esHost = esHost;
    }

    public Integer getEsPort() {
        return esPort;
    }

    public void setEsPort(Integer esPort) {
        this.esPort = esPort;
    }

    public void setJavaFilters(List<String> javaFilters) {
        this.javaFilters = javaFilters;
    }

    public List<String> getNpmFilters() {
        return npmFilters;
    }

    public void setNpmFilters(List<String> npmFilters) {
        this.npmFilters = npmFilters;
    }


}
