<template>
  <div class="card">
    <DataTable v-model:filters="filters" :value="sensorData" paginator showGridlines :rows="10"
               :rowsPerPageOptions="[10, 25, 50, 100]"
               dataKey="id" filterDisplay="menu" :loading="loading" :globalFilterFields="['temperature']">
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
          <Calendar class="mb-10px" v-model="filterModel.value" dateFormat="mm/dd/yy"
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
          <InputNumber v-model="filterModel.value" locale="en-US" :max-fraction-digits="2" :min-fraction-digits="2"/>
        </template>
      </Column>
      <Column field="humidity" dataType="numeric" header="Humidity" style="min-width: 12rem">
        <template #body="{ data }">
          {{ data.humidity }} %
        </template>
        <template #filter="{ filterModel }">
          <Dropdown class="mb-10px " v-model="filterModel.matchMode" :options="filterOptions" optionLabel="label"
                    optionValue="value"/>
          <InputNumber v-model="filterModel.value" locale="en-US" :max-fraction-digits="2" :min-fraction-digits="2"/>
        </template>
      </Column>
      <Column field="noise_level" dataType="numeric" header="Noise Level" style="min-width: 12rem">
        <template #body="{ data }">
          {{ data.noise_level }} dB
        </template>
        <template #filter="{ filterModel }">
          <Dropdown class="mb-10px " v-model="filterModel.matchMode" :options="filterOptions" optionLabel="label"
                    optionValue="value"/>
          <InputNumber v-model="filterModel.value" locale="en-US" :max-fraction-digits="2" :min-fraction-digits="2"/>
        </template>
      </Column>
      <Column field="heart_frequency" dataType="numeric" header="Heart Frequency" style="min-width: 12rem">
        <template #body="{ data }">
          {{ data.heart_frequency }} bpm
        </template>
        <template #filter="{ filterModel }">
          <Dropdown class="mb-10px " v-model="filterModel.matchMode" :options="filterOptions" optionLabel="label"
                    optionValue="value"/>
          <InputNumber v-model="filterModel.value"/>
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<script setup>
import {ref, defineProps } from 'vue';
import {FilterMatchMode, FilterOperator, FilterService} from 'primevue/api';
import DataTable from 'primevue/datatable'
import Button from "primevue/button";
import Column from "primevue/column";
import InputNumber from "primevue/inputnumber";
import Dropdown from 'primevue/dropdown';
import Calendar from "primevue/calendar";

defineProps({
  sensorData: Array
});

const timestampFilterOptions = ref([
  {label: 'Timestamp greater or equals to', value: "dateAfterOrEqual"},
  {label: 'Timestamp lower or equals to', value: "dateBeforeOrEqual"},
]);

const filterOptions = ref([
  {label: 'Equals', value: FilterMatchMode.EQUALS},
  {label: 'Greater Than', value: FilterMatchMode.GREATER_THAN},
  {label: 'Less Than', value: FilterMatchMode.LESS_THAN},
]);

const formatDate = (date) => {
  const day = date.getDate().toString().padStart(2, '0');
  const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Months are 0-indexed
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

  return value.getTime() > filter.getTime() || value.toDateString() === filter.toDateString();
}

function dateBeforeOrEqual(value, filter) {
  if (filter === undefined || filter === null) {
    return true;
  }
  if (value === undefined || value === null) {
    return false;
  }

  return value.getTime() < filter.getTime() || value.toDateString() === filter.toDateString();
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
const clearFilter = () => {
  initFilters();
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