<template>
  <div class="card">
    <DataTable v-model:filters="filters" :value="sensorData" paginator showGridlines :rows="10"
               @page="updateQueryString" :first="firstRow"
               @update:filters="updateQueryString" dataKey="id" filterDisplay="menu" :loading="loading">
      <template #header>
        <div class="w-10px">
          <Button type="button" icon="pi pi-filter-slash" label="Clear" @click="clearFilter()"/>
        </div>
      </template>
      <template #empty> No sensor data found.</template>
      <template #loading>Loading sensor data. Please wait.</template>
      <Column field="timestamp" header="Timestamp" dataType="date" style="min-width: 12rem">
        <template #body="{ data }">
          {{ formatDate(data.timestamp) }}
        </template>
        <template #filter="{ filterModel }">
          <Dropdown class="mb-10px " v-model="filterModel.matchMode" :options="timestampFilterOptions"
                    optionLabel="label"
                    optionValue="value"/>
          <Calendar class="mb-10px" v-model="filterModel.value" dateFormat="dd/mm/yy"
                    hourFormat="24" showTime placeholder="Timestamp" :manualInput="false"/>
        </template>
      </Column>
      <Column field="temperature" dataType="numeric" header="Temperature" style="min-width: 12rem">
        <template #body="{ data }">
          {{ data.temperature }} ºC
        </template>
        <template #filter="{ filterModel }">
          <Dropdown class="mb-10px " v-model="filterModel.matchMode" :options="filterOptions" optionLabel="label"
                    optionValue="value"/>
          <InputMask v-model="filterModel.value" mask="99.99"/>
        </template>
      </Column>
      <Column field="humidity" dataType="numeric" header="Humidity" style="min-width: 12rem">
        <template #body="{ data }">
          {{ data.humidity }} %
        </template>
        <template #filter="{ filterModel }">
          <Dropdown class="mb-10px " v-model="filterModel.matchMode" :options="filterOptions" optionLabel="label"
                    optionValue="value"/>
          <InputMask v-model="filterModel.value" mask="99.99"/>
        </template>
      </Column>
      <Column field="noise_level" dataType="numeric" header="Noise Level" style="min-width: 12rem">
        <template #body="{ data }">
          {{ data.noise_level }} dB
        </template>
        <template #filter="{ filterModel }">
          <Dropdown class="mb-10px " v-model="filterModel.matchMode" :options="filterOptions" optionLabel="label"
                    optionValue="value"/>
          <InputMask v-model="filterModel.value" mask="99.99"/>
        </template>
      </Column>
      <Column field="heart_frequency" dataType="numeric" header="Heart Frequency" style="min-width: 12rem">
        <template #body="{ data }">
          {{ data.heart_frequency == null ? "N/A" : data.heart_frequency + "bpm" }}
        </template>
        <template #filter="{ filterModel }">
          <Dropdown class="mb-10px " v-model="filterModel.matchMode" :options="filterOptions" optionLabel="label"
                    optionValue="value"/>
          <InputMask v-model="filterModel.value" mask="99?9"/>
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<script setup>
import {ref, defineProps, watch} from 'vue';
import {FilterMatchMode, FilterOperator, FilterService} from 'primevue/api';
import DataTable from 'primevue/datatable'
import Button from "primevue/button";
import Column from "primevue/column";
import Dropdown from 'primevue/dropdown';
import Calendar from "primevue/calendar";
import SessionQueriesService from "@/services/SessionQueriesService";
import InputMask from 'primevue/inputmask';

const props = defineProps({
  sensorData: Array,
  sessionQuery: Object
});

const firstRow = ref(1);
watch(() => props.sessionQuery, (newValue) => {
  if (newValue) {
    if (newValue["last_report_query"] !== null) {
      const params = new URLSearchParams(newValue["last_report_query"]);

      if (params.get("page") !== null) {
        firstRow.value = (parseInt(params.get("page")) - 1) * 10;
        activePage.value = params.get("page");
        queryString.value = "?page=" + activePage.value;
      } else {
        updateQueryString();
      }

      filters.value.timestamp.constraints = [];
      if (params.get("start_date") !== null) {
        filters.value.timestamp.constraints.push({
          value: new Date(params.get("start_date")),
          matchMode: "dateAfterOrEqual"
        });
      }

      if (params.get("end_date") !== null) {
        filters.value.timestamp.constraints.push({
          value: new Date(params.get("end_date")),
          matchMode: "dateBeforeOrEqual"
        });
      }
      if (filters.value.timestamp.constraints.length === 0) {
        filters.value.timestamp.constraints = [{value: null, matchMode: "dateAfterOrEqual"}];
      }

      const environmentalVariables = ["temperature", "humidity", "noise_level", "heart_frequency"];
      for (const environmentalVariable of environmentalVariables) {
        if (params.get(environmentalVariable) !== null && params.get(`${environmentalVariable}_condition`) !== null) {
          let condition = FilterMatchMode.EQUALS;
          switch (params.get(`${environmentalVariable}_condition`)) {
            case "gt":
              condition = FilterMatchMode.GREATER_THAN;
              break;
            case "lt":
              condition = FilterMatchMode.LESS_THAN;
          }
          let paramValue = params.get(environmentalVariable)
          if (paramValue.includes(".")) {
            paramValue = paramValue.split(".")
            if (paramValue[0].length === 1) {
              paramValue[0] = "0" + paramValue[0];
            }
            paramValue = paramValue[0] + paramValue[1];
          } else {
            if (paramValue.length === 1) {
              paramValue = "00" + paramValue;
            } else if (paramValue.length === 2) {
              paramValue = "0" + paramValue;
            }
          }
          console.log(environmentalVariable, paramValue)
          filters.value[environmentalVariable].constraints[0] = {
            value: paramValue,
            matchMode: condition
          };
        }
      }
    }
  }
});

const timestampFilterOptions = ref([
  {label: 'Timestamp after or equals to', value: "dateAfterOrEqual"},
  {label: 'Timestamp before or equals to', value: "dateBeforeOrEqual"},
]);

const filterOptions = ref([
  {label: 'Equals', value: FilterMatchMode.EQUALS},
  {label: 'Greater Than', value: FilterMatchMode.GREATER_THAN},
  {label: 'Less Than', value: FilterMatchMode.LESS_THAN},
]);

const activePage = ref(1);
const queryString = ref("");

const updateQueryString = async (event) => {
  console.log(event)
  if (event.page !== undefined) {
    activePage.value = event.page + 1;
  }
  const params = new URLSearchParams(queryString.value);
  params.set("page", activePage.value);

  if (event.timestamp !== undefined) {
    for (const queryStringKey in event) {
      if (event[queryStringKey].constraints[0].value !== null) {
        if (queryStringKey === "timestamp") {
          for (const constraint of event[queryStringKey].constraints) {
            let value = constraint.value;
            value = value.toISOString();
            value = value.slice(0, value.indexOf(".")) + "Z"
            if (constraint.matchMode === "dateAfterOrEqual") {
              params.set("start_date", value);
            } else if (constraint.matchMode === "dateBeforeOrEqual") {
              params.set("end_date", value);
            }
          }
        } else {
          let value = event[queryStringKey].constraints[0].value;
          let condition = event[queryStringKey].constraints[0].matchMode;
          if (event[queryStringKey].constraints[0].matchMode === "equals") {
            condition = "eq";
          }
          params.set(queryStringKey + "_condition", condition);
          params.set(queryStringKey, value);
        }
      } else {
        params.delete(queryStringKey);
      }
    }
  }
  queryString.value = "?" + params.toString();
  queryString.value = queryString.value.replaceAll("%3A", ":");
  await SessionQueriesService.saveSessionQuery({last_report_query: queryString.value});
};

const formatDate = (date) => {
  const day = date.getDate().toString().padStart(2, '0');
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const year = date.getFullYear();
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');

  return `${day}/${month}/${year} ${hours}:${minutes}`;
};

function dateAfterOrEqual(value, filter) {
  if (filter === undefined || filter === null) {
    return true;
  }
  if (value === undefined || value === null) {
    return false;
  }

  return value.getTime() > filter.getTime() || value.toString() === filter.toString();
}

function dateBeforeOrEqual(value, filter) {
  if (filter === undefined || filter === null) {
    return true;
  }
  if (value === undefined || value === null) {
    return false;
  }

  return value.getTime() < filter.getTime() || value.toString() === filter.toString();
}

FilterService.register('dateAfterOrEqual', dateAfterOrEqual);
FilterService.register('dateBeforeOrEqual', dateBeforeOrEqual);

const filters = ref();
const loading = ref(true);
loading.value = false;

const initFilters = () => {
  filters.value = {
    timestamp: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: "dateAfterOrEqual"}]},
    temperature: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]},
    humidity: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]},
    noise_level: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]},
    heart_frequency: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]},
  };
};


initFilters();

const clearFilter = async () => {
  initFilters();
  await updateQueryString({page: 0});
  window.location.reload();
};
</script>

<style>
.mb-10px {
  margin-bottom: 10px;
}

.w-10px {
  width: 10px;
}

.p-column-filter-operator-dropdown,
.p-column-filter-matchmode-dropdown,
.p-column-filter-add-rule {
  display: none;
}

#pv_id_1 .p-column-filter-add-rule {
  display: flex;
}

.p-column-filter-buttonbar {
  padding-top: 0;
}

</style>