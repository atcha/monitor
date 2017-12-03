let instance;

class ConfigurationStore {
  constructor() {
    if (instance) {
      return instance;
    }
    this.state = {
      activateElasticSearch: false,
      elasticSearch: {},
      javaFilters: [],
      npmFilters: []
    };
    instance = this;
  }

  get javaFilters() {
    return Array.from(this._state.javaFilters);
  }

  get npmFilters() {
    return Array.from(this._state.npmFilters);
  }

  importDb(data) {
    const body = JSON.stringify(data);

    return fetch('/api/configuration/db/import', {
      method: 'PUT',
      body
    });
  }

  initialize() {
    return fetch('/api/configuration')
      .then(res => res.json())
      .then(content => {
        this.state = content;
        return this.state;
      });
  }

  save(configuration) {
    const body = JSON.stringify(configuration);
    return fetch('/api/configuration', {
      method: 'PUT',
      body
    })
      .then(() => {
        this.state = configuration;
        return this.state;
      });
  }

  resetElasticSearch() {
    return fetch('/api/configuration/db/es', {
      method: 'PUT',
    })
      .then(res => res.json());
  }
}

export default new ConfigurationStore();
