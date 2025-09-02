// API Client for Electronic Department System
class ApiClient {
    constructor() {
        this.baseUrl = '';
        this.token = localStorage.getItem('jwt');
    }

    // Helper method to get auth headers
    getAuthHeaders() {
        const headers = {
            'Content-Type': 'application/json'
        };
        
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        
        return headers;
    }

    // Helper method for API calls
    async apiCall(endpoint, options = {}) {
        const url = `${this.baseUrl}${endpoint}`;
        const config = {
            headers: this.getAuthHeaders(),
            ...options
        };

        try {
            const response = await fetch(url, config);
            
            if (response.status === 401) {
                this.logout();
                return null;
            }

            // Handle different response types
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const data = await response.json();
                return { success: response.ok, data, status: response.status };
            } else {
                const text = await response.text();
                return { success: response.ok, data: text, status: response.status };
            }
        } catch (error) {
            console.error('API Error:', error);
            return { success: false, error: error.message };
        }
    }

    // Authentication methods
    async login(username, password) {
        const response = await this.apiCall('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });

        if (response?.success && response.data?.success) {
            this.token = response.data.message;
            localStorage.setItem('jwt', this.token);
        }

        return response;
    }

    async register(userData, password) {
        return await this.apiCall(`/api/auth/register?password=${encodeURIComponent(password)}`, {
            method: 'POST',
            body: JSON.stringify(userData)
        });
    }

    async getCurrentUser() {
        return await this.apiCall('/api/auth/current-user');
    }

    async logout() {
        await this.apiCall('/api/auth/logout', { method: 'POST' });
        this.token = null;
        localStorage.removeItem('jwt');
        window.location.href = '/login.html';
    }

    // User management
    async getUsers() {
        return await this.apiCall('/api/users');
    }

    async getUserById(id) {
        return await this.apiCall(`/api/users/${id}`);
    }

    async createUser(userData) {
        return await this.apiCall('/api/users', {
            method: 'POST',
            body: JSON.stringify(userData)
        });
    }

    async createUserWithPassword(userData, password) {
        return await this.apiCall(`/api/users/with-password?password=${encodeURIComponent(password)}`, {
            method: 'POST',
            body: JSON.stringify(userData)
        });
    }

    async updateUser(id, userData) {
        return await this.apiCall(`/api/users/${id}`, {
            method: 'PUT',
            body: JSON.stringify(userData)
        });
    }

    async deleteUser(id) {
        return await this.apiCall(`/api/users/${id}`, { method: 'DELETE' });
    }

    async activateUser(id) {
        return await this.apiCall(`/api/users/${id}/activate`, { method: 'PUT' });
    }

    async deactivateUser(id) {
        return await this.apiCall(`/api/users/${id}/deactivate`, { method: 'PUT' });
    }

    async updateUserPassword(id, password) {
        return await this.apiCall(`/api/users/${id}/password`, {
            method: 'PUT',
            body: JSON.stringify({ password })
        });
    }

    async searchUsers(query) {
        return await this.apiCall(`/api/users/search?q=${encodeURIComponent(query)}`);
    }

    async getUsersByRole(role) {
        return await this.apiCall(`/api/users/by-role/${role}`);
    }
    
    async getStudents() {
        return await this.apiCall('/api/public/students');
    }

    async getStudentsWithoutGroup() {
        return await this.apiCall('/api/students/without-group');
    }

    async searchStudentsWithoutGroup(query) {
        return await this.apiCall(`/api/students/search-without-group?name=${encodeURIComponent(query)}`);
    }

    async getStudent(studentId) {
        return await this.apiCall(`/api/students/${studentId}`);
    }

    async getStudentsByTeacher(teacherId) {
        return await this.apiCall(`/api/teachers/${teacherId}/students`);
    }
    
    // Groups management methods
    async getGroups() {
        return await this.apiCall('/api/groups');
    }
    
    async getGroupById(id) {
        return await this.apiCall(`/api/groups/${id}`);
    }
    
    async createGroup(groupData) {
        return await this.apiCall('/api/groups', {
            method: 'POST',
            body: JSON.stringify(groupData)
        });
    }
    
    async updateGroup(id, groupData) {
        return await this.apiCall(`/api/groups/${id}`, {
            method: 'PUT',
            body: JSON.stringify(groupData)
        });
    }
    
    async deleteGroup(id) {
        const response = await this.apiCall(`/api/groups/${id}`, {
            method: 'DELETE'
        });
        
        // Handle both success response and 204 No Content
        if (response?.success || response?.status === 204 || response?.status === 200) {
            return { success: true, data: response?.data };
        }
        
        return response;
    }
    
    async getActiveGroups() {
        return await this.apiCall('/api/groups/active');
    }
    
    async searchGroups(name) {
        return await this.apiCall(`/api/groups/search?name=${encodeURIComponent(name)}`);
    }
    
    async getGroupStudents(groupId) {
        return await this.apiCall(`/api/groups/${groupId}/students`);
    }
    
    async addStudentToGroup(groupId, studentId) {
        return await this.apiCall(`/api/groups/${groupId}/students/${studentId}`, {
            method: 'POST'
        });
    }
    
    async updateGroupStudents(groupId, studentIds) {
        return await this.apiCall(`/api/groups/${groupId}/students`, {
            method: 'PUT',
            body: JSON.stringify(studentIds)
        });
    }
    
    async removeStudentFromGroup(groupId, studentId) {
        return await this.apiCall(`/api/groups/${groupId}/students/${studentId}`, {
            method: 'DELETE'
        });
    }
    
    // Subject management methods
    async getSubjects() {
        return await this.apiCall('/api/subjects');
    }
    
    async createSubject(subjectData) {
        return await this.apiCall('/api/subjects', {
            method: 'POST',
            body: JSON.stringify(subjectData)
        });
    }
    
    async updateSubject(id, subjectData) {
        return await this.apiCall(`/api/subjects/${id}`, {
            method: 'PUT',
            body: JSON.stringify(subjectData)
        });
    }
    
    async deleteSubject(id) {
        return await this.apiCall(`/api/subjects/${id}`, {
            method: 'DELETE'
        });
    }
    
    async assignTeacherToSubject(subjectId, teacherId) {
        return await this.apiCall(`/api/subjects/${subjectId}/teachers/${teacherId}`, {
            method: 'POST'
        });
    }
    
    async removeTeacherFromSubject(subjectId, teacherId) {
        return await this.apiCall(`/api/subjects/${subjectId}/teachers/${teacherId}`, {
            method: 'DELETE'
        });
    }

    async getSubjectsByTeacher(teacherId) {
        return await this.apiCall(`/api/subjects/teacher/${teacherId}`);
    }

    // User management methods
    async getActiveUsers() {
        return await this.apiCall('/api/users/active');
    }

    // Grade management
    async getGrades() {
        return await this.apiCall('/api/grades');
    }

    async getGradeById(id) {
        return await this.apiCall(`/api/grades/${id}`);
    }

    async createGrade(gradeData) {
        console.log('OLD createGrade called with:', gradeData);
        return await this.apiCall('/api/grades', {
            method: 'POST',
            body: JSON.stringify(gradeData)
        });
    }

    async createGradeByIds(studentId, subjectId, teacherId, gradeType, value, comments = null) {
        console.log('createGradeByIds called with:', { studentId, subjectId, teacherId, gradeType, value, comments });
        return await this.apiCall('/api/grades/by-user-ids', {
            method: 'POST',
            body: JSON.stringify({
                studentId, // This is actually userId from frontend
                subjectId, 
                teacherId,
                gradeType,
                gradeValue: value,
                comments
            })
        });
    }

    async updateGrade(id, gradeData) {
        return await this.apiCall(`/api/grades/${id}`, {
            method: 'PUT',
            body: JSON.stringify(gradeData)
        });
    }

    async deleteGrade(id) {
        return await this.apiCall(`/api/grades/${id}`, { method: 'DELETE' });
    }

    async getGradesByStudent(studentId) {
        return await this.apiCall(`/api/grades/student/${studentId}`);
    }

    async getMyGrades() {
        return await this.apiCall('/api/grades/my-grades');
    }

    async getGradesBySubject(subjectId) {
        return await this.apiCall(`/api/grades/subject/${subjectId}`);
    }

    async getGradesByTeacher(teacherId) {
        return await this.apiCall(`/api/grades/teacher/${teacherId}`);
    }

    async getGradesByType(gradeType) {
        return await this.apiCall(`/api/grades/type/${gradeType}`);
    }

    async getStudentSubjectGrades(studentId, subjectId) {
        return await this.apiCall(`/api/grades/student/${studentId}/subject/${subjectId}`);
    }

    async getStudentSubjectAverage(studentId, subjectId) {
        return await this.apiCall(`/api/grades/student/${studentId}/subject/${subjectId}/average`);
    }

    async getStudentAverage(studentId) {
        return await this.apiCall(`/api/grades/student/${studentId}/average`);
    }

    async getFinalGradesByStudent(studentId) {
        return await this.apiCall(`/api/grades/student/${studentId}/final`);
    }

    // Public information
    async getPublicTeachers() {
        return await this.apiCall('/api/public/teachers');
    }

    async getPublicTeacherById(id) {
        return await this.apiCall(`/api/public/teachers/${id}`);
    }

    async searchPublicTeachers(query) {
        return await this.apiCall(`/api/public/teachers/search?q=${encodeURIComponent(query)}`);
    }

    async getPublicSubjects() {
        return await this.apiCall('/api/public/subjects');
    }

    async getPublicSubjectById(id) {
        return await this.apiCall(`/api/public/subjects/${id}`);
    }

    async searchPublicSubjects(query) {
        return await this.apiCall(`/api/public/subjects/search?q=${encodeURIComponent(query)}`);
    }

    async getSystemStatus() {
        return await this.apiCall('/api/public/status');
    }

    async getHealthCheck() {
        return await this.apiCall('/api/public/health');
    }

    async getDepartmentInfo() {
        return await this.apiCall('/api/public/department-info');
    }
}

// Create global instance
window.apiClient = new ApiClient();
