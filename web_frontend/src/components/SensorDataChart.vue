<template>
  <Card class="chart-card">
    <template #title>
      <div class="flex period-dropdown justify-content-start">
        <Dropdown :options="timePeriods" v-model="selectedPeriod" @change="updateChartData" optionLabel="name"/>
      </div>
      {{ sensorName }} measured data
    </template>
    <template #content>
      <div class="flex flex-column">
        <Chart type="line" :data="chartData" :options="chartOptions"/>
      </div>
    </template>
  </Card>
</template>

<script setup>
import 'chartjs-adapter-moment';
import {defineProps, ref} from 'vue';
import Dropdown from 'primevue/dropdown';
import Chart from 'primevue/chart';
import Card from "primevue/card";
import SensorService from "@/SensorService";

const props = defineProps({
  sensorName: String
});

const timePeriods = [
  {name: 'Last Hour', value: 'hour'},
  {name: 'Last Day', value: 'day'},
  {name: 'Last Week', value: 'week'}
];

const getChartUnit = () => {
  switch (selectedPeriod.value.value) {
    case "hour":
      return 'minute';
    case "day":
      return 'hour';
    case "week":
      return 'day';
  }
}

const getChartDisplayFormat = () => {
  switch (selectedPeriod.value.value) {
    case "hour":
      return {minute: 'HH:mm'};
    case "day":
      return {hour: 'HH:mm'};
    case "week":
      return {day: 'DD/MM HH:mm'};
  }
}

const selectedPeriod = ref(timePeriods[1]); // Default to 'Last Day'
const chartData = ref({});
const chartOptions = ref({
  scales: {
    x: {
      type: 'time',
      time: {
        parser: 'yyyy-MM-ddTHH:mm:ss',
        tooltipFormat: 'll HH:mm',
        unit: getChartUnit(),
        displayFormats: getChartDisplayFormat(),
      },
      title: {
        display: true,
        text: 'Date'
      }
    },
    y: {
      title: {
        display: true,
        text: props.sensorName
      }
    }
  }
});

function formatDate(date) {
  let dateStr = date.toISOString();
  return dateStr.replace(dateStr.slice(dateStr.indexOf(".")), "Z");
  // const year = date.getYear();
  // const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-indexed
  // const day = String(date.getDate()).padStart(2, '0');
  // const hours = String(date.getHours()).padStart(2, '0');
  // const minutes = String(date.getMinutes()).padStart(2, '0');

  // return `${year}-${month}-${day}T${hours}:${minutes}:00Z`;
}

const sensorData = ref([]);

const fetchData = async () => {
  let nowDate = new Date();
  let startDate = null;
  let endDate = formatDate(nowDate);
  if (selectedPeriod.value.value === "hour") {
    startDate = formatDate(new Date(nowDate.getTime() - (1 * 60 * 60 * 1000)));
  } else if (selectedPeriod.value.value === "day") {
    startDate = formatDate(new Date(nowDate.getTime() - (24 * 60 * 60 * 1000)));
  } else if (selectedPeriod.value.value === "week") {
    startDate = formatDate(new Date(nowDate.getTime() - (7 * 24 * 60 * 60 * 1000)));
  }

  try {
    const response = await SensorService.fetchSensorData({
      start_date: startDate,
      end_date: endDate,
    });

    sensorData.value = response["results"].map(item => ({
      ...item,
      timestamp:  new Date(item.timestamp)
    }));
    return sensorData.value;
  } catch (error) {
    console.error(error);
    return [];
  }
}

const getColorBasedOnSensorName = () => {
  switch (formatSensorName(props.sensorName)) {
    case "temperature":
      return 'rgb(255, 70, 50';
    case "humidity":
      return 'rgb(70, 130, 180)';
    case "noise_level":
      return 'rgb(240, 200, 80)';
    case "heart_frequency":
      return 'rgb(220, 20, 60)';
  }
}

const formatSensorName = (sensorName) => {
  sensorName = sensorName.toLowerCase();
  sensorName = sensorName.replaceAll(" ", "_");
  return sensorName
}

async function updateChartData() {
  const data = await fetchData();

  const timeUnit = getChartUnit();
  const displayFormat = getChartDisplayFormat();

  chartOptions.value.scales.x.time.unit = timeUnit;
  chartOptions.value.scales.x.time.displayFormats = displayFormat;

  const formattedData = data.map(item => ({
    x: item.timestamp,
    y: item[formatSensorName(props.sensorName)]
  }));

  chartData.value = {
    labels: formattedData.map(item => item.x),
    datasets: [
      {
        label: props.sensorName,
        data: formattedData,
        fill: false,
        borderColor: getColorBasedOnSensorName(),
        tension: 0.1
      }
    ]
  };
}

updateChartData(selectedPeriod.value); // Initial chart data setup
</script>

<style>
.chart-card {
  width: 28rem;
  margin-bottom: 1rem;
}

.period-dropdown {
  width: 50px;
  margin-bottom: 1rem;
  margin-left: 1rem;
}
</style>