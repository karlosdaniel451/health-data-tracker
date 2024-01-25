<template>
  <div class="charts-container">
    <SensorDataChart sensorName="Temperature" :sessionQuery="sessionQuery"/>
    <SensorDataChart sensorName="Humidity" :sessionQuery="sessionQuery"/>
    <SensorDataChart sensorName="Noise Level" :sessionQuery="sessionQuery"/>
    <SensorDataChart sensorName="Heart Frequency" :sessionQuery="sessionQuery"/>
  </div>
  <SensorDataTable :sensorData="sensorData" :sessionQuery="sessionQuery"/>
</template>

<script setup>
import {onBeforeMount, ref} from "vue";
import SensorService from "@/services/SensorService";
import SessionQueriesService from "@/services/SessionQueriesService";

const sensorData = ref([]);
const sessionQuery = ref();
onBeforeMount(() => {
  SessionQueriesService.fetchSessionQueries()
      .then(data => {
        sessionQuery.value = data[0];
      })
      .catch(error => console.error(error));
  SensorService.fetchSensorData()
      .then(data => {
        sensorData.value = data["results"].map(item => ({
          ...item,
          timestamp: new Date(item.timestamp)
        }));
      })
      .catch(error => console.error(error));
})
</script>

<script>
import SensorDataTable from "@/components/SensorDataTable.vue";
import SensorDataChart from "@/components/SensorDataChart.vue";

export default {
  name: 'App',
  components: {
    SensorDataChart,
    SensorDataTable,
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

.charts-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 1rem; /* Adjust the gap between cards */
}
</style>
