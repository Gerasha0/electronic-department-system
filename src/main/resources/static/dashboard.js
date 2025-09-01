// Dashboard JavaScript
// Initialize API client
const apiClient = new ApiClient();

class Dashboard {
    constructor() {
        this.currentUser = null;
        this.currentSection = 'overview';
        this.init();
    }

    async init() {
        // Check authentication
        if (!apiClient.token) {
            window.location.href = '/login.html';
            return;
        }

        // Load current user
        await this.loadCurrentUser();
        
        // Setup event listeners
        this.setupEventListeners();
        
        // Load initial data
        await this.loadOverviewData();
        
        // Configure interface based on user role
        this.configureByRole();
    }

    async loadCurrentUser() {
        const response = await apiClient.getCurrentUser();
        if (response?.success) {
            this.currentUser = response.data;
            this.updateUserInfo();
        } else {
            window.location.href = '/login.html';
        }
    }

    updateUserInfo() {
        if (this.currentUser) {
            document.getElementById('user-name').textContent = 
                `${this.currentUser.firstName} ${this.currentUser.lastName} (${this.currentUser.role})`;
        }
    }

    isLoggedIn() {
        return this.currentUser != null;
    }

    configureByRole() {
        const role = this.currentUser?.role;
        
        // New role-based navigation configuration
        const navButtons = {
            'users-nav': ['ADMIN'],                                    // Only ADMIN
            'teachers-nav': ['ADMIN', 'MANAGER'],                     // ADMIN, MANAGER  
            'students-nav': ['ADMIN', 'MANAGER', 'TEACHER'],          // ADMIN, MANAGER, TEACHER
            'groups-nav': ['ADMIN', 'MANAGER', 'TEACHER'],            // ADMIN, MANAGER, TEACHER (read-only for TEACHER)
            'subjects-nav': ['ADMIN', 'MANAGER', 'TEACHER', 'STUDENT'], // All authenticated users
            'grades-nav': ['ADMIN', 'MANAGER', 'TEACHER', 'STUDENT']  // All authenticated users
        };

        // Hide/show navigation based on role
        Object.entries(navButtons).forEach(([navId, allowedRoles]) => {
            const navElement = document.getElementById(navId);
            if (navElement) {
                navElement.style.display = allowedRoles.includes(role) ? 'block' : 'none';
            }
        });

        // Configure action buttons based on role
        this.configureActionButtonsByRole(role);

        // Configure UI elements visibility
        this.configureActionButtons();

        // Role-specific initial section
        if (role === 'STUDENT') {
            this.showSection('grades');  // Students see their grades first
        } else if (role === 'TEACHER') {
            this.showSection('subjects'); // Teachers see their subjects first
        } else {
            this.showSection('overview'); // ADMIN, MANAGER see overview
        }
    }

    configureActionButtonsByRole(role) {
        // Configuration for different action buttons based on role
        const buttonConfig = {
            'ADMIN': {
                canCreate: true,
                canEdit: true,
                canDelete: true,
                canActivate: true
            },
            'MANAGER': {
                canCreate: true,    // Can create students, teachers, groups, subjects
                canEdit: true,      // Can edit students, teachers, groups, subjects
                canDelete: false,   // Cannot delete (only ADMIN)
                canActivate: true   // Can activate/deactivate
            },
            'TEACHER': {
                canCreate: false,   // Cannot create
                canEdit: false,     // Cannot edit (except own grades)
                canDelete: false,   // Cannot delete
                canActivate: false  // Cannot activate/deactivate
            },
            'STUDENT': {
                canCreate: false,   // Cannot create
                canEdit: false,     // Cannot edit
                canDelete: false,   // Cannot delete
                canActivate: false  // Cannot activate/deactivate
            }
        };

        this.rolePermissions = buttonConfig[role] || buttonConfig['STUDENT'];
    }

    setupEventListeners() {
        // Navigation
        document.querySelectorAll('.nav-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const section = e.target.dataset.section;
                this.showSection(section);
            });
        });

        // Logout
        document.getElementById('logout-btn').addEventListener('click', () => {
            apiClient.logout();
        });

        // Search and filter handlers
        this.setupSearchAndFilters();
        
        // Modal handlers
        this.setupModalHandlers();
    }

    setupSearchAndFilters() {
        // User search
        document.getElementById('search-users')?.addEventListener('click', () => {
            this.searchUsers();
        });

        // Teacher search
        document.getElementById('search-teachers')?.addEventListener('click', () => {
            this.searchTeachers();
        });

        // Student search
        document.getElementById('search-students')?.addEventListener('click', () => {
            this.searchStudents();
        });

        // Group search
        document.getElementById('search-groups')?.addEventListener('click', () => {
            this.searchGroups();
        });

        // Subject search        // Add subject button
        document.getElementById('add-subject')?.addEventListener('click', () => {
            this.showAddSubjectModal();
        });

        // Add group button
        document.getElementById('add-group')?.addEventListener('click', () => {
            this.showAddGroupModal();
        });

        // Grade filters
        document.getElementById('filter-grades')?.addEventListener('click', () => {
            this.filterGrades();
        });

        // Add buttons
        document.getElementById('add-user-btn')?.addEventListener('click', () => {
            this.showAddUserModal();
        });

        document.getElementById('add-grade-btn')?.addEventListener('click', () => {
            this.showAddGradeModal();
        });

        // Subject form submission
        document.getElementById('subjectForm')?.addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleSubjectSubmit();
        });
    }

    setupModalHandlers() {
        const modal = document.getElementById('modal');
        const closeBtn = document.querySelector('.close');

        closeBtn?.addEventListener('click', () => {
            modal.style.display = 'none';
        });

        window.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.style.display = 'none';
            }
        });
    }

    showSection(sectionName) {
        // Hide all sections
        document.querySelectorAll('.section').forEach(section => {
            section.classList.remove('active');
        });

        // Remove active class from nav buttons
        document.querySelectorAll('.nav-btn').forEach(btn => {
            btn.classList.remove('active');
        });

        // Show selected section
        const section = document.getElementById(`${sectionName}-section`);
        const navBtn = document.querySelector(`[data-section="${sectionName}"]`);
        
        if (section) {
            section.classList.add('active');
            this.currentSection = sectionName;
        }
        
        if (navBtn) {
            navBtn.classList.add('active');
        }

        // Load section data
        this.loadSectionData(sectionName);
    }

    async loadSectionData(sectionName) {
        switch (sectionName) {
            case 'overview':
                await this.loadOverviewData();
                break;
            case 'users':
                await this.loadUsersData();
                break;
            case 'grades':
                await this.loadGradesData();
                break;
            case 'teachers':
                await this.loadTeachersData();
                break;
            case 'students':
                await this.loadStudentsData();
                break;
            case 'groups':
                await this.loadGroupsData();
                break;
            case 'subjects':
                await this.loadSubjectsData();
                break;
        }
    }

    async loadOverviewData() {
        // Load system stats
        const statsDiv = document.getElementById('system-stats');
        if (statsDiv) {
            try {
                const statusResponse = await apiClient.getSystemStatus();
                const healthResponse = await apiClient.getHealthCheck();
                
                let statsHtml = '';
                if (statusResponse?.success) {
                    statsHtml += `<p>📊 Статус системи: <span class="status-badge status-active">Активна</span></p>`;
                }
                if (healthResponse?.success) {
                    statsHtml += `<p>💚 Стан здоров'я: <span class="status-badge status-active">Ок</span></p>`;
                }
                
                statsDiv.innerHTML = statsHtml || '<p>Дані недоступні</p>';
            } catch (error) {
                statsDiv.innerHTML = '<p>Помилка завантаження статистики</p>';
            }
        }

        // Load user profile
        const profileDiv = document.getElementById('user-profile');
        if (profileDiv && this.currentUser) {
            profileDiv.innerHTML = `
                <p><strong>Ім'я:</strong> ${this.currentUser.firstName} ${this.currentUser.lastName}</p>
                <p><strong>Email:</strong> ${this.currentUser.email}</p>
                <p><strong>Роль:</strong> <span class="role-badge role-${this.currentUser.role.toLowerCase()}">${this.currentUser.role}</span></p>
                <p><strong>Статус:</strong> <span class="status-badge ${this.currentUser.isActive ? 'status-active' : 'status-inactive'}">${this.currentUser.isActive ? 'Активний' : 'Неактивний'}</span></p>
            `;
        }

        // Load department info
        const deptDiv = document.getElementById('department-info');
        if (deptDiv) {
            try {
                const response = await apiClient.getDepartmentInfo();
                if (response?.success) {
                    const info = response.data;
                    deptDiv.innerHTML = `
                        <p><strong>Назва:</strong> ${info.name || 'Електронна кафедра'}</p>
                        <p><strong>Опис:</strong> ${info.description || 'Система управління навчальним процесом'}</p>
                    `;
                } else {
                    deptDiv.innerHTML = '<p>Інформація про кафедру недоступна</p>';
                }
            } catch (error) {
                deptDiv.innerHTML = '<p>Помилка завантаження інформації</p>';
            }
        }

        // Load recent activities
        const activitiesDiv = document.getElementById('recent-activities');
        if (activitiesDiv) {
            activitiesDiv.innerHTML = `
                <p>📝 Останні дії будуть відображатися тут</p>
                <p>🕒 Система запущена: ${new Date().toLocaleString('uk-UA')}</p>
            `;
        }
    }

    async loadUsersData() {
        const tbody = document.getElementById('users-tbody');
        if (!tbody) return;

        tbody.innerHTML = '<tr><td colspan="6"><div class="loading"></div></td></tr>';

        try {
            const response = await apiClient.getUsers();
            if (response?.success && Array.isArray(response.data)) {
                this.renderUsersTable(response.data);
            } else {
                tbody.innerHTML = '<tr><td colspan="6">Помилка завантаження користувачів</td></tr>';
            }
        } catch (error) {
            tbody.innerHTML = '<tr><td colspan="6">Помилка завантаження даних</td></tr>';
        }
    }

    renderUsersTable(users) {
        const tbody = document.getElementById('users-tbody');
        if (!tbody) return;

        if (!users.length) {
            tbody.innerHTML = '<tr><td colspan="6">Користувачі не знайдені</td></tr>';
            return;
        }

        tbody.innerHTML = users.map(user => `
            <tr>
                <td>${user.firstName} ${user.lastName}</td>
                <td>${user.email}</td>
                <td><span class="role-badge role-${user.role.toLowerCase()}">${user.role}</span></td>
                <td><span class="status-badge ${user.isActive ? 'status-active' : 'status-inactive'}">${user.isActive ? 'Активний' : 'Неактивний'}</span></td>
                <td>${new Date(user.createdAt).toLocaleDateString('uk-UA')}</td>
                <td>
                    <div class="table-actions">
                        <button class="btn btn-sm btn-primary" onclick="dashboard.editUser(${user.id})">Редагувати</button>
                        <button class="btn btn-sm ${user.isActive ? 'btn-warning' : 'btn-success'}" onclick="dashboard.toggleUserStatus(${user.id}, ${user.isActive})">
                            ${user.isActive ? 'Деактивувати' : 'Активувати'}
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="dashboard.deleteUser(${user.id})">Видалити</button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    async loadGradesData() {
        const tbody = document.getElementById('grades-tbody');
        if (!tbody) return;

        tbody.innerHTML = '<tr><td colspan="6"><div class="loading"></div></td></tr>';

        try {
            let response;
            
            // Role-based grade loading
            if (this.currentUser?.role === 'STUDENT') {
                // Students see only their own grades - use special endpoint
                response = await apiClient.getMyGrades();
            } else if (this.currentUser?.role === 'TEACHER') {
                // Teachers see only grades for their subjects
                if (this.currentUser.teacherId) {
                    response = await apiClient.getGradesByTeacher(this.currentUser.teacherId);
                } else {
                    tbody.innerHTML = '<tr><td colspan="6">Профіль викладача не знайдено</td></tr>';
                    return;
                }
            } else {
                // ADMIN and MANAGER see all grades
                response = await apiClient.getGrades();
            }

            if (response?.success && Array.isArray(response.data)) {
                this.renderGradesTable(response.data);
            } else {
                tbody.innerHTML = '<tr><td colspan="6">Помилка завантаження оцінок</td></tr>';
            }
        } catch (error) {
            tbody.innerHTML = '<tr><td colspan="6">Помилка завантаження даних</td></tr>';
        }
    }

    renderGradesTable(grades) {
        const tbody = document.getElementById('grades-tbody');
        if (!tbody) return;

        if (!grades.length) {
            tbody.innerHTML = '<tr><td colspan="6">Оцінки не знайдені</td></tr>';
            return;
        }

        tbody.innerHTML = grades.map(grade => {
            // Use fields directly from API response
            const studentName = grade.studentName || 'N/A';
            const subjectName = grade.subjectName || 'N/A';
            const gradeType = grade.gradeType || 'N/A';
            const gradeValue = grade.gradeValue || 'N/A';
            const gradeDate = grade.gradeDate || grade.createdAt;
            const formattedDate = gradeDate ? new Date(gradeDate).toLocaleDateString('uk-UA') : 'N/A';

            return `
            <tr>
                <td>${studentName}</td>
                <td>${subjectName}</td>
                <td>${gradeType}</td>
                <td><strong>${gradeValue}</strong></td>
                <td>${formattedDate}</td>
                <td>
                    <div class="table-actions">
                        ${this.currentUser?.role !== 'STUDENT' ? `
                            <button class="btn btn-sm btn-primary" onclick="dashboard.editGrade(${grade.id})">Редагувати</button>
                            <button class="btn btn-sm btn-danger" onclick="dashboard.deleteGrade(${grade.id})">Видалити</button>
                        ` : ''}
                    </div>
                </td>
            </tr>
        `;
        }).join('');
    }

    async loadTeachersData() {
        const tbody = document.getElementById('teachers-tbody');
        if (!tbody) return;

        tbody.innerHTML = '<tr><td colspan="5"><div class="loading"></div></td></tr>';

        try {
            const response = await apiClient.getPublicTeachers();
            if (response?.success && Array.isArray(response.data)) {
                this.renderTeachersTable(response.data);
            } else {
                tbody.innerHTML = '<tr><td colspan="5">Помилка завантаження викладачів</td></tr>';
            }
        } catch (error) {
            tbody.innerHTML = '<tr><td colspan="5">Помилка завантаження даних</td></tr>';
        }
    }

    renderTeachersTable(teachers) {
        const tbody = document.getElementById('teachers-tbody');
        if (!tbody) return;

        if (!teachers.length) {
            tbody.innerHTML = '<tr><td colspan="5">Викладачі не знайдені</td></tr>';
            return;
        }

        tbody.innerHTML = teachers.map(teacher => {
            // Extract proper fields from teacher object
            const fullName = teacher.user ? `${teacher.user.firstName} ${teacher.user.lastName}` : (teacher.fullName || 'N/A');
            const email = teacher.user ? teacher.user.email : 'N/A';
            const department = teacher.departmentPosition || 'N/A';
            const subjects = teacher.subjects ? teacher.subjects.map(s => s.subjectName).join(', ') : 'N/A';
            
            return `
            <tr>
                <td>${fullName}</td>
                <td>${email}</td>
                <td>${department}</td>
                <td>${subjects}</td>
                <td>
                    <div class="table-actions">
                        <button class="btn btn-sm btn-primary" onclick="dashboard.viewTeacher(${teacher.id})">Переглянути</button>
                    </div>
                </td>
            </tr>
        `;
        }).join('');
    }

    async loadStudentsData() {
        const tbody = document.getElementById('students-tbody');
        if (!tbody) return;

        tbody.innerHTML = '<tr><td colspan="6"><div class="loading"></div></td></tr>';

        try {
            const response = await apiClient.getStudents();
            if (response?.success && Array.isArray(response.data)) {
                this.renderStudentsTable(response.data);
            } else {
                tbody.innerHTML = '<tr><td colspan="6">Помилка завантаження студентів</td></tr>';
            }
        } catch (error) {
            tbody.innerHTML = '<tr><td colspan="6">Помилка завантаження даних</td></tr>';
        }
    }

    renderStudentsTable(students) {
        const tbody = document.getElementById('students-tbody');
        if (!tbody) return;

        if (!students.length) {
            tbody.innerHTML = '<tr><td colspan="6">Студенти не знайдені</td></tr>';
            return;
        }

        tbody.innerHTML = students.map(student => {
            // Extract proper fields from student object
            const fullName = student.user ? `${student.user.firstName} ${student.user.lastName}` : (student.fullName || 'N/A');
            const email = student.user ? student.user.email : 'N/A';
            const groupName = student.group ? student.group.groupName : 'Не призначено';
            const course = student.course || 'N/A';
            const averageGrade = student.averageGrade !== undefined ? 
                (student.averageGrade > 0 ? student.averageGrade.toFixed(2) : '0.00') : 'N/A';
            
            return `
            <tr>
                <td>${fullName}</td>
                <td>${email}</td>
                <td>${groupName}</td>
                <td>${course}</td>
                <td>${averageGrade}</td>
                <td>
                    <div class="table-actions">
                        <button class="btn btn-sm btn-primary" onclick="dashboard.viewStudentGrades(${student.id})">Оцінки</button>
                    </div>
                </td>
            </tr>
        `;
        }).join('');
    }

    // === GROUPS MANAGEMENT ===
    async loadGroupsData() {
        const tbody = document.getElementById('groups-tbody');
        if (!tbody) return;

        tbody.innerHTML = '<tr><td colspan="7"><div class="loading"></div></td></tr>';

        try {
            const response = await apiClient.getGroups();
            if (response?.success && Array.isArray(response.data)) {
                this.renderGroupsTable(response.data);
            } else {
                tbody.innerHTML = '<tr><td colspan="7">Помилка завантаження груп</td></tr>';
            }
        } catch (error) {
            console.error('Помилка завантаження груп:', error);
            tbody.innerHTML = '<tr><td colspan="7">Помилка завантаження даних</td></tr>';
        }
    }

    renderGroupsTable(groups) {
        const tbody = document.getElementById('groups-tbody');
        if (!tbody) return;

        if (!groups.length) {
            tbody.innerHTML = '<tr><td colspan="7">Групи не знайдені</td></tr>';
            return;
        }

        tbody.innerHTML = groups.map(group => {
            const groupName = group.groupName || 'N/A';
            const groupCode = group.groupCode || 'N/A';
            const courseYear = group.courseYear || 'N/A';
            const studyForm = group.studyForm || 'N/A';
            const studentCount = group.currentStudentCount || 0;
            const enrollmentYear = group.enrollmentYear || group.startYear || 'N/A';
            
            // Check role permissions for action buttons
            const canEdit = this.rolePermissions?.canEdit || false;
            const canDelete = this.rolePermissions?.canDelete || false;
            
            let actions = `<button class="btn btn-sm btn-info" onclick="dashboard.viewGroupStudents(${group.id})">Студенти</button>`;
            
            if (canEdit) {
                actions += ` <button class="btn btn-sm btn-primary" onclick="dashboard.editGroup(${group.id})">Редагувати</button>`;
            }
            
            if (canDelete) {
                actions += ` <button class="btn btn-sm btn-danger" onclick="dashboard.deleteGroup(${group.id})">Видалити</button>`;
            }
            
            return `
            <tr>
                <td>${groupName}</td>
                <td>${groupCode}</td>
                <td>${courseYear}</td>
                <td>${studyForm}</td>
                <td>${studentCount}</td>
                <td>${enrollmentYear}</td>
                <td>
                    <div class="table-actions">
                        ${actions}
                    </div>
                </td>
            </tr>
        `;
        }).join('');
    }

    async viewGroupStudents(groupId) {
        // Implementation for viewing students in a group
        console.log('Viewing students in group:', groupId);
        // TODO: Implement group students view
    }

    async editGroup(groupId) {
        // Implementation for editing group
        console.log('Editing group:', groupId);
        // TODO: Implement group editing
    }

    async deleteGroup(groupId) {
        // Implementation for deleting group
        console.log('Deleting group:', groupId);
        // TODO: Implement group deletion
    }

    async loadSubjectsData() {
        const tbody = document.getElementById('subjects-tbody');
        if (!tbody) return;

        tbody.innerHTML = '<tr><td colspan="5"><div class="loading"></div></td></tr>';

        try {
            let response;
            
            // Role-based subject loading
            if (this.currentUser?.role === 'TEACHER') {
                // Teachers see only their own subjects
                response = await apiClient.getSubjectsByTeacher(this.currentUser.teacherId);
            } else {
                // Everyone else sees public subjects
                response = await apiClient.getPublicSubjects();
            }
                
            // Check if response is successful and has data
            if (response?.success && Array.isArray(response.data)) {
                this.renderSubjectsTable(response.data);
            } else if (Array.isArray(response)) {
                // Direct array response from public API
                this.renderSubjectsTable(response);
            } else {
                tbody.innerHTML = '<tr><td colspan="5">Помилка завантаження дисциплін</td></tr>';
            }
        } catch (error) {
            console.error('Помилка завантаження дисциплін:', error);
            tbody.innerHTML = '<tr><td colspan="5">Помилка завантаження даних</td></tr>';
        }
    }

    renderSubjectsTable(subjects) {
        const tbody = document.getElementById('subjects-tbody');
        if (!tbody) return;

        if (!subjects.length) {
            tbody.innerHTML = '<tr><td colspan="5">Дисципліни не знайдені</td></tr>';
            return;
        }

        tbody.innerHTML = subjects.map(subject => {
            // Extract proper fields from subject object
            const subjectName = subject.subjectName || 'N/A';
            const teacherName = subject.teachers && subject.teachers.length > 0 
                ? subject.teachers.map(t => t.user ? `${t.user.firstName} ${t.user.lastName}` : t.fullName).join(', ')
                : 'N/A';
            const credits = subject.credits || 'N/A';
            const semester = subject.semester || 'N/A';
            
            return `
            <tr>
                <td>${subjectName}</td>
                <td>${teacherName}</td>
                <td>${credits}</td>
                <td>${semester}</td>
                <td>
                    <div class="table-actions">
                        <button class="btn btn-sm btn-primary" onclick="dashboard.viewSubject(${subject.id})">Переглянути</button>
                        <button class="btn btn-sm btn-warning" onclick="dashboard.editSubject(${subject.id})">Редагувати</button>
                        <button class="btn btn-sm btn-danger" onclick="dashboard.deleteSubject(${subject.id})">Видалити</button>
                    </div>
                </td>
            </tr>
        `;
        }).join('');
    }

    // Modal methods
    showAddUserModal() {
        const modal = document.getElementById('modal');
        const title = document.getElementById('modal-title');
        const body = document.getElementById('modal-body');

        title.textContent = 'Додати користувача';
        body.innerHTML = `
            <form id="add-user-form">
                <div class="form-group">
                    <label>Ім'я:</label>
                    <input type="text" name="firstName" required>
                </div>
                <div class="form-group">
                    <label>Прізвище:</label>
                    <input type="text" name="lastName" required>
                </div>
                <div class="form-group">
                    <label>Email:</label>
                    <input type="email" name="email" required>
                </div>
                <div class="form-group">
                    <label>Ім'я користувача:</label>
                    <input type="text" name="username" required>
                </div>
                <div class="form-group">
                    <label>Пароль:</label>
                    <input type="password" name="password" required>
                </div>
                <div class="form-group">
                    <label>Роль:</label>
                    <select name="role" required>
                        <option value="STUDENT">Студент</option>
                        <option value="TEACHER">Викладач</option>
                        <option value="MANAGER">Менеджер</option>
                        <option value="ADMIN">Адміністратор</option>
                        <option value="GUEST">Гість</option>
                    </select>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn btn-success">Створити</button>
                    <button type="button" class="btn btn-secondary" onclick="document.getElementById('modal').style.display='none'">Скасувати</button>
                </div>
            </form>
        `;

        // Handle form submission
        document.getElementById('add-user-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const userData = Object.fromEntries(formData);
            const password = userData.password;
            delete userData.password;

            const response = await apiClient.createUserWithPassword(userData, password);
            if (response?.success) {
                modal.style.display = 'none';
                this.loadUsersData();
                alert('Користувача створено успішно!');
            } else {
                const errorMessage = typeof response?.data === 'string' 
                    ? response.data 
                    : (response?.data?.message || response?.error || 'Невідома помилка');
                alert('Помилка створення користувача: ' + errorMessage);
            }
        });

        modal.style.display = 'block';
    }

    showAddGradeModal() {
        const modal = document.getElementById('modal');
        const title = document.getElementById('modal-title');
        const body = document.getElementById('modal-body');

        title.textContent = 'Додати оцінку';
        body.innerHTML = `
            <form id="add-grade-form">
                <div class="form-group">
                    <label>Студент:</label>
                    <select name="studentId" id="grade-student-select" required>
                        <option value="">Оберіть студента...</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Дисципліна:</label>
                    <select name="subjectId" id="grade-subject-select" required>
                        <option value="">Оберіть дисципліну...</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Тип оцінки:</label>
                    <select name="gradeType" required>
                        <option value="">Оберіть тип...</option>
                        <option value="CURRENT">Поточна</option>
                        <option value="MODULE">Модульна</option>
                        <option value="MIDTERM">Проміжна</option>
                        <option value="FINAL">Підсумкова</option>
                        <option value="RETAKE">Перездача</option>
                        <option value="MAKEUP">Відпрацювання</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Оцінка:</label>
                    <input type="number" name="value" min="1" max="100" required>
                </div>
                <div class="form-group">
                    <label>Коментар (необов'язково):</label>
                    <textarea name="comment" rows="3"></textarea>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn btn-success">Додати оцінку</button>
                    <button type="button" class="btn btn-secondary" onclick="document.getElementById('modal').style.display='none'">Скасувати</button>
                </div>
            </form>
        `;

        // Load students and subjects for dropdowns
        this.loadStudentsForGrades();
        this.loadSubjectsForGrades();

        // Clear any existing event listeners
        const existingForm = document.getElementById('add-grade-form');
        if (existingForm) {
            existingForm.replaceWith(existingForm.cloneNode(true));
        }

        // Handle form submission
        document.getElementById('add-grade-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            
            const studentId = parseInt(formData.get('studentId'));
            const subjectId = parseInt(formData.get('subjectId'));
            const teacherId = 1; // Hardcoded for now, should be current teacher
            const gradeType = formData.get('gradeType');
            const value = parseInt(formData.get('value'));
            const comments = formData.get('comment') || null;

            try {
                const response = await apiClient.createGradeByIds(studentId, subjectId, teacherId, gradeType, value, comments);
                if (response && response.success && response.data && response.data.id) {
                    modal.style.display = 'none';
                    this.loadGradesData();
                    alert('Оцінку додано успішно!');
                } else {
                    throw new Error(response?.data?.message || response?.error || 'Невідома помилка');
                }
            } catch (error) {
                console.error('Error creating grade:', error);
                const errorMessage = error.message || 'An unexpected error occurred';
                alert('Помилка додавання оцінки: ' + errorMessage);
            }
        });

        modal.style.display = 'block';
    }

    showEditGradeModal(grade) {
        const modal = document.getElementById('modal');
        const title = document.getElementById('modal-title');
        const body = document.getElementById('modal-body');

        title.textContent = 'Редагувати оцінку';
        body.innerHTML = `
            <form id="edit-grade-form">
                <div class="form-group">
                    <label>Студент:</label>
                    <select name="studentId" id="edit-grade-student-select" required>
                        <option value="">Оберіть студента...</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Дисципліна:</label>
                    <select name="subjectId" id="edit-grade-subject-select" required>
                        <option value="">Оберіть дисципліну...</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Тип оцінки:</label>
                    <select name="gradeType" required>
                        <option value="">Оберіть тип...</option>
                        <option value="CURRENT">Поточна</option>
                        <option value="MODULE">Модульна</option>
                        <option value="MIDTERM">Проміжна</option>
                        <option value="FINAL">Підсумкова</option>
                        <option value="RETAKE">Перездача</option>
                        <option value="MAKEUP">Відпрацювання</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Оцінка:</label>
                    <input type="number" name="value" min="1" max="100" value="${grade.value || ''}" required>
                </div>
                <div class="form-group">
                    <label>Коментар (необов'язково):</label>
                    <textarea name="comment" rows="3">${grade.comment || ''}</textarea>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Зберегти зміни</button>
                    <button type="button" class="btn btn-secondary" onclick="document.getElementById('modal').style.display='none'">Скасувати</button>
                </div>
            </form>
        `;

        // Load students and subjects for dropdowns, then set current values
        this.loadStudentsForGrades('edit-grade-student-select', grade.studentUserId || grade.studentId);
        this.loadSubjectsForGrades('edit-grade-subject-select', grade.subjectId);
        
        // Set current grade type
        setTimeout(() => {
            const gradeTypeSelect = document.querySelector('#edit-grade-form select[name="gradeType"]');
            if (gradeTypeSelect && grade.gradeType) {
                gradeTypeSelect.value = grade.gradeType;
            }
        }, 100);

        // Handle form submission
        document.getElementById('edit-grade-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const gradeData = {
                studentId: grade.studentId, // Use original studentId (Student entity ID) for backend
                subjectId: parseInt(formData.get('subjectId')),
                gradeType: formData.get('gradeType'),
                gradeValue: parseInt(formData.get('value')),
                comments: formData.get('comment') || null
            };

            const response = await apiClient.updateGrade(grade.id, gradeData);
            if (response?.success) {
                modal.style.display = 'none';
                this.loadGradesData();
                alert('Оцінку оновлено успішно!');
            } else {
                const errorMessage = typeof response?.data === 'string' 
                    ? response.data 
                    : (response?.data?.message || response?.error || 'Невідома помилка');
                alert('Помилка оновлення оцінки: ' + errorMessage);
            }
        });

        modal.style.display = 'block';
    }

    async loadStudentsForGrades(selectId = 'grade-student-select', selectedId = null) {
        try {
            const response = await apiClient.getUsersByRole('STUDENT');
            const select = document.getElementById(selectId);
            if (select && response?.success) {
                const students = response.data || [];
                select.innerHTML = '<option value="">Оберіть студента...</option>' +
                    students.map(student => 
                        `<option value="${student.id}" ${selectedId == student.id ? 'selected' : ''}>
                            ${student.firstName} ${student.lastName}
                        </option>`
                    ).join('');
            }
        } catch (error) {
            console.error('Error loading students:', error);
        }
    }

    async loadSubjectsForGrades(selectId = 'grade-subject-select', selectedId = null) {
        try {
            const response = await apiClient.getPublicSubjects();
            const select = document.getElementById(selectId);
            if (select && response?.success) {
                const subjects = response.data || [];
                select.innerHTML = '<option value="">Оберіть дисципліну...</option>' +
                    subjects.map(subject => 
                        `<option value="${subject.id}" ${selectedId == subject.id ? 'selected' : ''}>
                            ${subject.subjectName || 'Без назви'}
                        </option>`
                    ).join('');
            }
        } catch (error) {
            console.error('Error loading subjects:', error);
        }
    }

    // Action methods
    async editGrade(gradeId) {
        try {
            const response = await apiClient.getGradeById(gradeId);
            if (response?.success) {
                this.showEditGradeModal(response.data);
            } else {
                alert('Помилка завантаження оцінки');
            }
        } catch (error) {
            alert('Помилка: ' + error.message);
        }
    }

    async deleteGrade(gradeId) {
        if (confirm('Ви впевнені, що хочете видалити цю оцінку?')) {
            try {
                const response = await apiClient.deleteGrade(gradeId);
                if (response?.success) {
                    alert('Оцінку видалено успішно!');
                    this.loadGradesData();
                } else {
                    alert('Помилка видалення оцінки: ' + (response?.data || 'Невідома помилка'));
                }
            } catch (error) {
                alert('Помилка: ' + error.message);
            }
        }
    }

    async toggleUserStatus(userId, isActive) {
        const action = isActive ? 'deactivateUser' : 'activateUser';
        const response = await apiClient[action](userId);
        
        if (response?.success) {
            this.loadUsersData();
            alert(`Користувача ${isActive ? 'деактивовано' : 'активовано'} успішно!`);
        } else {
            alert('Помилка зміни статусу користувача');
        }
    }

    async deleteUser(userId) {
        if (confirm('Ви впевнені, що хочете видалити цього користувача?')) {
            const response = await apiClient.deleteUser(userId);
            if (response?.success) {
                this.loadUsersData();
                alert('Користувача видалено успішно!');
            } else {
                alert('Помилка видалення користувача');
            }
        }
    }

    // Search methods
    async searchUsers() {
        const query = document.getElementById('user-search').value;
        const role = document.getElementById('user-filter-role').value;

        if (query) {
            const response = await apiClient.searchUsers(query);
            if (response?.success) {
                this.renderUsersTable(response.data || []);
            }
        } else if (role) {
            const response = await apiClient.getUsersByRole(role);
            if (response?.success) {
                this.renderUsersTable(response.data || []);
            }
        } else {
            this.loadUsersData();
        }
    }

    async searchTeachers() {
        const query = document.getElementById('teacher-search').value;
        if (query) {
            const response = await apiClient.searchPublicTeachers(query);
            if (response?.success) {
                this.renderTeachersTable(response.data || []);
            }
        } else {
            this.loadTeachersData();
        }
    }

    async searchSubjects() {
        const query = document.getElementById('subject-search').value;
        if (query) {
            const response = await apiClient.searchPublicSubjects(query);
            if (response?.success) {
                this.renderSubjectsTable(response.data || []);
            }
        } else {
            this.loadSubjectsData();
        }
    }

    filterGrades() {
        // Implementation for grade filtering
        this.loadGradesData();
    }

    // Missing functions
    editUser(userId) {
        alert(`Редагування користувача ID: ${userId}. Функція буде реалізована пізніше.`);
    }

    viewTeacher(teacherId) {
        alert(`Перегляд викладача ID: ${teacherId}. Функція буде реалізована пізніше.`);
    }

    viewSubject(subjectId) {
        alert(`Перегляд предмету ID: ${subjectId}. Функція буде реалізована пізніше.`);
    }

    // Subject management methods
    showAddSubjectModal() {
        // Reset form for new subject
        document.getElementById('subjectForm').reset();
        this.currentSubject = null;
        this.showModal('subjectModal');
    }

    async createSubject() {
        const form = document.getElementById('subject-form');
        const formData = new FormData(form);
        
        const subjectData = {
            subjectName: formData.get('subjectName'),
            subjectCode: formData.get('subjectCode'),
            credits: parseInt(formData.get('credits')),
            semester: parseInt(formData.get('semester')),
            hoursTotal: parseInt(formData.get('hoursTotal')),
            hoursLectures: parseInt(formData.get('hoursLectures')) || 0,
            hoursPractical: parseInt(formData.get('hoursPractical')) || 0,
            hoursLaboratory: parseInt(formData.get('hoursLaboratory')) || 0,
            assessmentType: formData.get('assessmentType'),
            description: formData.get('description') || '',
            isActive: true
        };

        try {
            const response = await apiClient.createSubject(subjectData);
            if (response.success) {
                this.closeModal();
                this.loadSubjectsData();
                this.showSuccessMessage('Дисципліна успішно створена!');
            } else {
                this.showErrorMessage('Помилка при створенні дисципліни: ' + response.error);
            }
        } catch (error) {
            console.error('Error creating subject:', error);
            this.showErrorMessage('Помилка при створенні дисципліни');
        }
    }

    async editSubject(subjectId) {
        try {
            // Get subject data
            const response = await apiClient.getSubjects();
            if (response.success) {
                const subject = response.data.find(s => s.id === subjectId);
                if (subject) {
                    this.showEditSubjectModal(subject);
                }
            }
        } catch (error) {
            console.error('Error loading subject for edit:', error);
            this.showErrorMessage('Помилка при завантаженні дисципліни');
        }
    }

    showEditSubjectModal(subject) {
        this.showSubjectModal(subject);
    }

    async updateSubject() {
        const form = document.getElementById('subject-form');
        const formData = new FormData(form);
        const subjectId = document.getElementById('subject-id').value;
        
        const subjectData = {
            subjectName: formData.get('subjectName'),
            subjectCode: formData.get('subjectCode'),
            credits: parseInt(formData.get('credits')),
            semester: parseInt(formData.get('semester')),
            hoursTotal: parseInt(formData.get('hoursTotal')),
            hoursLectures: parseInt(formData.get('hoursLectures')) || 0,
            hoursPractical: parseInt(formData.get('hoursPractical')) || 0,
            hoursLaboratory: parseInt(formData.get('hoursLaboratory')) || 0,
            assessmentType: formData.get('assessmentType'),
            description: formData.get('description') || '',
            isActive: true
        };

        try {
            const response = await apiClient.updateSubject(subjectId, subjectData);
            if (response.success) {
                this.closeModal();
                this.loadSubjectsData();
                this.showSuccessMessage('Дисципліна успішно оновлена!');
            } else {
                this.showErrorMessage('Помилка при оновленні дисципліни: ' + response.error);
            }
        } catch (error) {
            console.error('Error updating subject:', error);
            this.showErrorMessage('Помилка при оновленні дисципліни');
        }
    }

    async deleteSubject(subjectId) {
        if (confirm('Ви впевнені, що хочете видалити цю дисципліну?')) {
            try {
                const response = await apiClient.deleteSubject(subjectId);
                if (response.success) {
                    this.loadSubjectsData();
                    this.showSuccessMessage('Дисципліна успішно видалена!');
                } else {
                    this.showErrorMessage('Помилка при видаленні дисципліни: ' + response.error);
                }
            } catch (error) {
                console.error('Error deleting subject:', error);
                this.showErrorMessage('Помилка при видаленні дисципліни');
            }
        }
    }

    async handleSubjectSubmit() {
        const form = document.getElementById('subjectForm');
        const formData = new FormData(form);
        
        const subjectData = {
            subjectName: formData.get('subjectName'),
            subjectCode: formData.get('subjectCode'),
            credits: parseInt(formData.get('credits')),
            semester: parseInt(formData.get('semester')),
            hoursTotal: parseInt(formData.get('hoursTotal')),
            hoursLectures: parseInt(formData.get('hoursLectures')) || 0,
            hoursPractical: parseInt(formData.get('hoursPractical')) || 0,
            hoursLaboratory: parseInt(formData.get('hoursLaboratory')) || 0,
            assessmentType: formData.get('assessmentType'),
            description: formData.get('description') || ''
        };

        const teacherId = formData.get('teacherId') || document.getElementById('subject-teacher')?.value;

        try {
            let response;
            if (this.currentSubject) {
                // Update existing subject
                response = await apiClient.updateSubject(this.currentSubject.id, subjectData);
            } else {
                // Create new subject
                response = await apiClient.createSubject(subjectData);
            }

            if (response?.success || response?.id) {
                const subjectId = response.id || this.currentSubject.id;
                
                // Assign teacher if selected
                if (teacherId) {
                    try {
                        await apiClient.assignTeacherToSubject(subjectId, teacherId);
                    } catch (error) {
                        console.error('Помилка призначення викладача:', error);
                        alert('Дисципліну створено, але не вдалося призначити викладача');
                    }
                }
                
                this.closeSubjectModal();
                this.loadSubjectsData();
                alert(this.currentSubject ? 'Дисципліну оновлено!' : 'Дисципліну створено!');
            } else {
                alert('Помилка при збереженні дисципліни');
            }
        } catch (error) {
            console.error('Помилка при збереженні дисципліни:', error);
            alert('Помилка при збереженні дисципліни');
        }
    }

    // Generic modal methods
    showModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'block';
        }
    }

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'none';
        }
    }

    // Show subject modal with data
    showSubjectModal(subject = null) {
        this.currentSubject = subject;
        
        // Load teachers for dropdown
        this.loadTeachersForSubject();
        
        if (subject) {
            document.getElementById('subject-name').value = subject.subjectName || '';
            document.getElementById('subject-code').value = subject.subjectCode || '';
            document.getElementById('subject-credits').value = subject.credits || '';
            document.getElementById('subject-semester').value = subject.semester || '';
            document.getElementById('subject-hours-total').value = subject.hoursTotal || '';
            document.getElementById('subject-hours-lectures').value = subject.hoursLectures || 0;
            document.getElementById('subject-hours-practical').value = subject.hoursPractical || 0;
            document.getElementById('subject-hours-laboratory').value = subject.hoursLaboratory || 0;
            document.getElementById('subject-assessment-type').value = subject.assessmentType || 'EXAM';
            document.getElementById('subject-description').value = subject.description || '';
            
            // Set teacher if assigned
            if (subject.teachers && subject.teachers.length > 0) {
                document.getElementById('subject-teacher').value = subject.teachers[0].id;
            }
        } else {
            document.getElementById('subjectForm').reset();
        }
        
        this.showModal('subjectModal');
    }

    async loadTeachersForSubject() {
        try {
            const response = await apiClient.getPublicTeachers();
            const teachers = Array.isArray(response) ? response : (response?.data || []);
            
            const teacherSelect = document.getElementById('subject-teacher');
            if (!teacherSelect) {
                console.error('Teacher select element not found');
                return;
            }
            
            teacherSelect.innerHTML = '<option value="">Оберіть викладача (необов\'язково)</option>';
            
            teachers.forEach(teacher => {
                const option = document.createElement('option');
                option.value = teacher.id;
                // Use fullName, displayTitle, or construct from user data
                const name = teacher.fullName || teacher.displayTitle || 
                           (teacher.user ? `${teacher.user.firstName} ${teacher.user.lastName}` : 'Невідомий викладач');
                option.textContent = name;
                teacherSelect.appendChild(option);
            });
            
            console.log(`Завантажено ${teachers.length} викладачів`);
        } catch (error) {
            console.error('Помилка завантаження викладачів:', error);
        }
    }

    closeSubjectModal() {
        this.currentSubject = null;
        this.closeModal('subjectModal');
    }

    // === SEARCH METHODS ===
    async searchStudents() {
        const searchInput = document.getElementById('student-search');
        const searchTerm = searchInput?.value?.trim();
        
        if (!searchTerm) {
            await this.loadStudentsData();
            return;
        }
        
        // TODO: Implement student search API call
        console.log('Searching students:', searchTerm);
    }

    async searchGroups() {
        const searchInput = document.getElementById('group-search');
        const searchTerm = searchInput?.value?.trim();
        
        if (!searchTerm) {
            await this.loadGroupsData();
            return;
        }
        
        try {
            const response = await apiClient.searchGroups(searchTerm);
            if (response?.success && Array.isArray(response.data)) {
                this.renderGroupsTable(response.data);
            }
        } catch (error) {
            console.error('Помилка пошуку груп:', error);
        }
    }

    // === GROUP MODAL METHODS ===
    showAddGroupModal() {
        if (!this.rolePermissions?.canCreate) {
            alert('У вас немає прав для створення груп');
            return;
        }
        
        // TODO: Implement group modal
        console.log('Show add group modal');
    }

    // === ROLE-BASED UI CONFIGURATION ===
    configureActionButtons() {
        // Configure action buttons based on user role
        const addButtons = {
            'add-group': this.rolePermissions?.canCreate,
            'add-subject': this.rolePermissions?.canCreate,
            'add-user-btn': this.rolePermissions?.canCreate
        };

        Object.entries(addButtons).forEach(([buttonId, visible]) => {
            const button = document.getElementById(buttonId);
            if (button) {
                button.style.display = visible ? 'inline-block' : 'none';
            }
        });
    }
}

// Initialize dashboard when page loads
document.addEventListener('DOMContentLoaded', () => {
    window.dashboard = new Dashboard();
});
