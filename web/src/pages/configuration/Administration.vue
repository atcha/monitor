<template>
  <div class="administration-page">
    <div>
      <h6>Base de données</h6>
    </div>
    <div>
      <div>
        <q-toggle v-model="activateElasticSearch" :value="true" color="teal-8" label="Elastic Search"></q-toggle>
      </div>
      <div v-if="activateElasticSearch" class="row">
        <div class="col es-input">
          <q-input type="text" v-model="elasticSearch.host" float-label="Host of elastic search"/>
        </div>
        <div class="col es-input">
          <q-input type="number" v-model="elasticSearch.port" float-label="Port of elastic search"/>

        </div>
      </div>
    </div>
    <div>
      <h6>Filtress</h6>
    </div>
    <div>
      <p class="caption">Filtres sur les librairies Java : </p>
      <q-chips-input color="green" v-model="javaFilters" placeholder="Ajouter un nouveau filtre"></q-chips-input>
    </div>
    <hr>
    <div>
      <p class="caption">Filtres sur les dépendences Npm :</p>
      <q-chips-input color="blue-grey" v-model="npmFilters" placeholder="Ajouter un nouveau filtre"></q-chips-input>
    </div>
    <hr>
    <div class="buttons">
      <div class="button">
        <q-btn flat color="black" @click="refresh" icon="refresh">Rafraîchir</q-btn>
      </div>
      <div class="button">
        <q-btn color="primary" @click="save" icon="save">Sauvegarder</q-btn>
      </div>
    </div>
  </div>
</template>
<script>
  import {QChipsInput, QBtn, QToggle, QInput} from 'quasar';
  import {success, error} from '../../Toasts'
  import ConfigurationStore from '../../stores/ConfigurationStore';

  export default {
    name: 'ConfigurationAdministration',
    components: {QChipsInput, QBtn, QToggle, QInput},
    data() {
      return {
        activateElasticSearch: false,
        elasticSearch: {},
        javaFilters: [],
        npmFilters: []
      }
    },
    methods: {
      refresh() {
        const {javaFilters, npmFilters, activateElasticSearch, elasticSearch} = ConfigurationStore.state;
        this.javaFilters = javaFilters;
        this.npmFilters = npmFilters;
        this.activateElasticSearch = activateElasticSearch;
        this.elasticSearch = elasticSearch;
      },
      save() {
        const {javaFilters, npmFilters, activateElasticSearch, elasticSearch} = this;
        const configuration = Object.assign({}, ConfigurationStore.state, {
          javaFilters,
          npmFilters,
          activateElasticSearch,
          elasticSearch
        });
        ConfigurationStore.save(configuration)
          .then(() => success())
          .catch((err) => error(err));
      }
    },
    mounted() {
      ConfigurationStore.initialize()
        .then(() => {
          this.refresh();
        })
        .catch((err) => console.log(err))
    }
  }
</script>
<style scoped>
  .administration-page {
    margin-top: 2em;
  }

  .administration-page .es-input {
    padding: 5px;
  }

  .administration-page .buttons {
    margin-top: 2em;
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }

  .administration-page .buttons .button {
    margin-left: 1em;
    margin-right: 1em;
  }
</style>
