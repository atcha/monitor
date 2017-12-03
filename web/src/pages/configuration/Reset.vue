<template>
  <div class="reset-page">
    <q-transition appear
                  enter="fadeIn"
                  leave="fadeOut">
      <div v-show="show">
      <div>
        <h6>Elastic Search</h6>
      </div>
      <q-btn icon="refresh" class="btn-100" big no-caps color="secondary" @click="doResetElasticSearch">
        Reset de la structure
      </q-btn>
      <hr>
      <div>
        <h6>Réimportation</h6>
      </div>
      <p class="caption text-red-7">
        Attention ! Lors d'une opération de rejoue des évènements l'applicatif sera indisponible. L'action de fin n'est
        visible uniquement dans les logs du serveurs.
      </p>
      <q-btn icon="import_export" class="btn-100" big no-caps color="red" @click="doReimport">
        Réimporter l'ensemble des données
      </q-btn>
      </div>
    </q-transition>
    <q-inner-loading :visible="loading" >
      <q-spinner-gears size="50px" color="primary"></q-spinner-gears>
    </q-inner-loading>
  </div>
</template>
<script>
  import {QBtn, QTransition, QInnerLoading, QSpinnerGears} from 'quasar';
  import {success, error} from '../../Toasts'
  import AppsStore from '../../stores/AppsStore';
  import ConfigurationStore from '../../stores/ConfigurationStore';

  export default {
    name: 'ConfigurationReset',
    components: {QBtn, QTransition, QInnerLoading, QSpinnerGears},
    data() {
      return {
        show: true,
        loading: false,
      };
    },
    methods: {
      doReimport() {
        AppsStore.remove()
          .then(() => {
            success();
          })
          .catch((err) => {
            error(err);
          })
      },
      doResetElasticSearch() {
        this.loading = true;
        this.show = false;
        ConfigurationStore.resetElasticSearch()
          .then((obj) => {
            if (obj.active) {
              const l = obj.created.reduce((acc, s) => {
                if (acc === '')
                  return s;
                return `${acc}, ${s}`;
              }, '');
              success(`<strong>Collection mise à jour :${l}</strong>`);
            } else {
              error(null, `<strong>Elastic Search non actif</strong>`)
            }
            this.loading = false;
            this.show = true;
          })
          .catch((err) => {
            error(err);
            this.loading = false;
            this.show = true;
          });
      }
    }
  }
</script>
<style scoped>
  .reset-page {
    margin-top: 2em;
  }

  .reset-page .btn-100 {
    width: 100%;
  }
</style>
