class SessionQueriesService {
    constructor(baseUrl = 'http://127.0.0.1:8000/session-queries/') {
        this.baseUrl = baseUrl;
    }

    async saveSessionQuery(sessionQuery) {
        try {
            const headers = {'Content-Type': 'application/json'};

            const response = await fetch(this.baseUrl, {
                method: 'POST',
                headers,
                body: JSON.stringify(sessionQuery),
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error("Error posting session data:", error);
            throw error;
        }
    }

    async fetchSessionQueries() {
        try {
            const response = await fetch(this.baseUrl);
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

export default new SessionQueriesService();
