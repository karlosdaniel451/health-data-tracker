class SensorService {
    constructor(baseUrl = 'http://127.0.0.1:8000/sensor-data/') {
        this.baseUrl = baseUrl;
    }

    buildQueryString(params) {
        if (!params || Object.keys(params).length === 0) {
            return '';
        }

        return Object.keys(params)
            .map(key => encodeURIComponent(key) + '=' + encodeURIComponent(params[key]))
            .join('&');
    }

    async fetchSensorData(queryParams = {}) {
        try {
            const queryString = this.buildQueryString(queryParams);
            const url = queryString ? `${this.baseUrl}?${queryString}` : this.baseUrl;

            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error("Error fetching sensor data:", error);
            throw error;
        }
    }
}

export default new SensorService();
