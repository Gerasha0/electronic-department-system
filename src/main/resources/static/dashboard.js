// Dashboard JavaScript
// Initialize API client
const apiClient = new ApiClient();

class Dashboard {
    constructor() {
        this.currentUser = null;
        this.currentSection = 'overview';
        this.allGrades = []; // Store all grades for filtering
        this.allStudents = []; // Store all students for filtering
        this.userSearchTimeout = null; // For debouncing user search
        this.teacherSearchTimeout = null; // For debouncing teacher search
        this.studentSearchTimeout = null; // For debouncing student search
        this.groupSearchTimeout = null; // For debouncing group search
        this.subjectSearchTimeout = null; // For debouncing subject search
        this.init();
    }

    // Translate grade types to Ukrainian
    translateGradeType(gradeType) {
        const translations = {
            // Grade evaluation types
            'CURRENT': '📝 Поточна',
            'MODULE': '📊 Модульна', 
            'MIDTERM': '⚡ Проміжна',
            'FINAL': '🏆 Підсумкова',
            'RETAKE': '🔄 Перездача',
            'MAKEUP': '📝 Відпрацювання',
            // Assessment types
            'EXAM': '📝 Іспит',
            'CREDIT': '✅ Залік',
            'COURSEWORK': '📚 Курсова',
            'LABORATORY': '🔬 Лабораторна',
            'PRACTICAL': '🛠️ Практична',
            'HOMEWORK': '📖 Домашнє завдання'
        };
        return translations[gradeType] || gradeType;
    }

    async init() {
        // Check authentication
        if (!apiClient.token) {
            window.location.href = '/login.html';
            return;
        }

        try {
            // Add loading state to navigation
            const navigation = document.querySelector('.nav-buttons');
            if (navigation) {
                navigation.classList.add('nav-loading');
            }

            // Load current user
            await this.loadCurrentUser();
            
            // Setup role-based navigation BEFORE showing it
            this.setupRoleBasedNavigation(this.currentUser?.role);
            
            // Setup event listeners
            this.setupEventListeners();
            
            // Load initial data
            await this.loadOverviewData();
            
            // Configure interface based on user role
            this.configureByRole();

            // Remove loading state and show navigation
            if (navigation) {
                navigation.classList.remove('nav-loading');
            }

            // Load default section based on role
            this.loadDefaultSection();

        } catch (error) {
            console.error('Error initializing dashboard:', error);
            // Remove loading state even on error
            const navigation = document.querySelector('.nav-buttons');
            if (navigation) {
                navigation.classList.remove('nav-loading');
            }
            window.location.href = '/login.html';
        }
    }

    loadDefaultSection() {
        // Load appropriate default section based on user role
        switch (this.currentUser?.role) {
            case 'ADMIN':
                this.showSection('users');
                break;
            case 'MANAGER':
                this.showSection('groups');
                break;
            case 'TEACHER':
                this.showSection('grades');
                break;
            case 'STUDENT':
                this.showSection('grades');
                break;
            case 'GUEST':
                this.showSection('subjects');
                break;
            default:
                this.showSection('subjects'); // fallback
        }
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

    setupRoleBasedNavigation(role) {
        // Configuration of which navigation buttons are visible for each role
        const navButtons = {
            'overview-nav': ['ADMIN', 'MANAGER'],                     // ADMIN and MANAGER
            'users-nav': ['ADMIN'],                                   // Only ADMIN
            'teachers-nav': ['ADMIN', 'MANAGER', 'GUEST'],           // ADMIN, MANAGER, GUEST
            'students-nav': ['ADMIN', 'MANAGER', 'TEACHER'],         // ADMIN, MANAGER, TEACHER
            'groups-nav': ['ADMIN', 'MANAGER', 'TEACHER'],           // ADMIN, MANAGER, TEACHER
            'subjects-nav': ['ADMIN', 'MANAGER', 'TEACHER', 'STUDENT', 'GUEST'], // All users
            'grades-nav': ['ADMIN', 'MANAGER', 'STUDENT'],           // All except GUEST and TEACHER
            'archive-nav': ['ADMIN']                                  // Only ADMIN
        };

        // Get all navigation buttons
        const allNavButtons = document.querySelectorAll('.nav-button');
        
        // First, hide all buttons by removing role-enabled class
        allNavButtons.forEach(button => {
            button.classList.remove('role-enabled');
        });

        // Then show only buttons allowed for this role
        Object.entries(navButtons).forEach(([navId, allowedRoles]) => {
            const navElement = document.getElementById(navId);
            if (navElement && allowedRoles.includes(role)) {
                navElement.classList.add('role-enabled');
            }
        });
    }

    configureByRole() {
        const role = this.currentUser?.role;
        
        // Configure action buttons based on role
        this.configureActionButtonsByRole(role);

        // Configure UI elements visibility
        this.configureActionButtons();
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
            },
            'GUEST': {
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

        // User search input - real-time search
        document.getElementById('user-search')?.addEventListener('input', () => {
            this.debounceUserSearch();
        });

        // User search input - Enter key
        document.getElementById('user-search')?.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.searchUsers();
            }
        });

        // User role filter
        document.getElementById('user-filter-role')?.addEventListener('change', () => {
            this.searchUsers();
        });

        // Teacher search
        document.getElementById('search-teachers')?.addEventListener('click', () => {
            this.searchTeachers();
        });

        // Teacher search input - real-time search
        document.getElementById('teacher-search')?.addEventListener('input', () => {
            this.debounceTeacherSearch();
        });

        // Teacher search input - Enter key
        document.getElementById('teacher-search')?.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.searchTeachers();
            }
        });

        // Student search
        document.getElementById('search-students')?.addEventListener('click', () => {
            this.searchStudents();
        });

        // Student search input - real-time search
        document.getElementById('student-search')?.addEventListener('input', () => {
            this.debounceStudentSearch();
        });

        // Student search input - Enter key
        document.getElementById('student-search')?.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.searchStudents();
            }
        });

        // Student filters
        document.getElementById('student-filter-group')?.addEventListener('change', () => {
            this.filterStudents();
        });

        document.getElementById('student-filter-grade-min')?.addEventListener('input', () => {
            this.filterStudents();
        });

        document.getElementById('student-filter-grade-max')?.addEventListener('input', () => {
            this.filterStudents();
        });

        document.getElementById('student-sort')?.addEventListener('change', () => {
            this.sortAndFilterStudents();
        });

        // Group search
        document.getElementById('search-groups')?.addEventListener('click', () => {
            this.searchGroups();
        });

        // Group search input - real-time search
        document.getElementById('group-search')?.addEventListener('input', () => {
            this.debounceGroupSearch();
        });

        // Group search input - Enter key
        document.getElementById('group-search')?.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.searchGroups();
            }
        });

        // Group filters
        document.getElementById('education-level-filter')?.addEventListener('change', () => {
            this.filterGroups();
        });

        document.getElementById('course-filter')?.addEventListener('change', () => {
            this.filterGroups();
        });

        document.getElementById('study-form-filter')?.addEventListener('change', () => {
            this.filterGroups();
        });

        document.getElementById('enrollment-year-filter')?.addEventListener('change', () => {
            this.filterGroups();
        });

        // Subject search
        document.getElementById('search-subjects')?.addEventListener('click', () => {
            this.searchSubjects();
        });

        // Subject search input - real-time search
        document.getElementById('subject-search')?.addEventListener('input', () => {
            this.debounceSubjectSearch();
        });

        // Subject search input - Enter key
        document.getElementById('subject-search')?.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.searchSubjects();
            }
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

        // Grade search input - filter on type
        document.getElementById('grade-search-student')?.addEventListener('input', () => {
            this.filterGrades();
        });

        // Grade filter dropdowns - filter on change
        document.getElementById('grade-filter-subject')?.addEventListener('change', () => {
            this.filterGrades();
        });

        document.getElementById('grade-filter-group')?.addEventListener('change', () => {
            this.filterGrades();
        });

        document.getElementById('grade-filter-type')?.addEventListener('change', () => {
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
                this.setupGradesFilters();
                break;
            case 'teachers':
                await this.loadTeachersData();
                break;
            case 'students':
                await this.loadStudentsData();
                await this.loadStudentFilters();
                break;
            case 'groups':
                await this.loadGroupsData();
                break;
            case 'subjects':
                await this.loadSubjectsData();
                break;
            case 'archive':
                await this.loadArchiveData();
                this.setupArchiveTabs();
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
                this.allGrades = response.data; // Store all grades
                this.renderGradesTable(response.data);
                this.loadSubjectsForFilter(); // Load subjects for filter
                this.loadGroupsForFilter(); // Load groups for filter
            } else {
                tbody.innerHTML = '<tr><td colspan="6">Помилка завантаження оцінок</td></tr>';
            }
        } catch (error) {
            tbody.innerHTML = '<tr><td colspan="6">Помилка завантаження даних</td></tr>';
        }
    }

    renderGradesTable(grades) {
        const tbody = document.getElementById('grades-tbody');
        const table = document.getElementById('grades-table');
        if (!tbody || !table) return;

        // Check if admin/manager should see actions column
        const role = this.currentUser?.role;
        const showActions = role === 'TEACHER'; // Only teachers can edit/delete grades
        const colCount = showActions ? 6 : 5;

        // Update table header based on permissions
        const thead = table.querySelector('thead tr');
        if (thead) {
            thead.innerHTML = `
                <th>🎓 Студент</th>
                <th>📚 Дисципліна</th>
                <th>📋 Тип</th>
                <th>⭐ Оцінка</th>
                <th>📅 Дата</th>
                ${showActions ? '<th>⚙️ Дії</th>' : ''}
            `;
        }

        if (!grades.length) {
            tbody.innerHTML = `<tr><td colspan="${colCount}">Оцінки не знайдені</td></tr>`;
            return;
        }

        tbody.innerHTML = grades.map(grade => {
            // Use fields directly from API response
            const studentName = grade.studentName || 'N/A';
            const subjectName = grade.subjectName || 'N/A';
            const gradeType = this.translateGradeType(grade.gradeType) || 'N/A';
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
                ${showActions ? `
                <td>
                    <div class="table-actions">
                        <button class="btn btn-sm btn-primary" onclick="dashboard.editGrade(${grade.id})">Редагувати</button>
                        <button class="btn btn-sm btn-danger" onclick="dashboard.deleteGrade(${grade.id})">Видалити</button>
                    </div>
                </td>
                ` : ''}
            </tr>
        `;
        }).join('');
    }

    // Load subjects for filter dropdown
    async loadSubjectsForFilter() {
        const select = document.getElementById('grade-filter-subject');
        if (!select) return;

        try {
            const response = await apiClient.getPublicSubjects();
            const subjects = Array.isArray(response) ? response : (response?.data || []);
            
            // Clear existing options except "Всі дисципліни"
            select.innerHTML = '<option value="">Всі дисципліни</option>';
            
            subjects.forEach(subject => {
                const option = document.createElement('option');
                option.value = subject.id;
                option.textContent = subject.subjectName;
                select.appendChild(option);
            });
        } catch (error) {
            console.error('Error loading subjects for filter:', error);
        }
    }

    // Load groups for filter dropdown
    async loadGroupsForFilter() {
        const select = document.getElementById('grade-filter-group');
        if (!select) return;

        try {
            const response = await apiClient.getGroups();
            const groups = Array.isArray(response) ? response : (response?.data || []);
            
            // Clear existing options except "Всі групи"
            select.innerHTML = '<option value="">Всі групи</option>';
            
            groups.forEach(group => {
                const option = document.createElement('option');
                option.value = group.id;
                option.textContent = group.name;
                select.appendChild(option);
            });
        } catch (error) {
            console.error('Error loading groups for filter:', error);
        }
    }

    // Load groups for student filter dropdown
    async loadGroupsForStudentFilter() {
        const select = document.getElementById('student-filter-group');
        if (!select) return;

        try {
            const response = await apiClient.getGroups();
            const groups = Array.isArray(response) ? response : (response?.data || []);
            
            // Clear existing options except "Всі групи"
            select.innerHTML = '<option value="">Всі групи</option>';
            
            groups.forEach(group => {
                const option = document.createElement('option');
                option.value = group.id;
                option.textContent = group.name;
                select.appendChild(option);
            });
        } catch (error) {
            console.error('Error loading groups for student filter:', error);
        }
    }

    // Filter grades based on current filter values
    filterGrades() {
        const subjectFilter = document.getElementById('grade-filter-subject')?.value || '';
        const studentSearch = document.getElementById('grade-search-student')?.value.toLowerCase() || '';
        const groupFilter = document.getElementById('grade-filter-group')?.value || '';
        const typeFilter = document.getElementById('grade-filter-type')?.value || '';

        let filteredGrades = this.allGrades;

        // Filter by subject
        if (subjectFilter) {
            filteredGrades = filteredGrades.filter(grade => 
                grade.subjectId && grade.subjectId.toString() === subjectFilter
            );
        }

        // Search by student name
        if (studentSearch) {
            filteredGrades = filteredGrades.filter(grade => 
                grade.studentName && grade.studentName.toLowerCase().includes(studentSearch)
            );
        }

        // Filter by group
        if (groupFilter) {
            filteredGrades = filteredGrades.filter(grade => 
                grade.groupId && grade.groupId.toString() === groupFilter
            );
        }

        // Filter by grade type
        if (typeFilter) {
            filteredGrades = filteredGrades.filter(grade => 
                grade.gradeType === typeFilter
            );
        }

        this.renderGradesTable(filteredGrades);
    }

    setupGradesFilters() {
        // Hide student search for STUDENT role since they only see their own grades
        const studentSearchInput = document.getElementById('grade-search-student');
        if (studentSearchInput) {
            const isStudent = this.currentUser?.role === 'STUDENT';
            studentSearchInput.style.display = isStudent ? 'none' : 'block';
        }
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
            let response;
            
            // Role-based student loading
            if (this.currentUser?.role === 'TEACHER') {
                // Teachers see only students from their subjects
                if (this.currentUser.teacherId) {
                    response = await apiClient.getStudentsByTeacher(this.currentUser.teacherId);
                } else {
                    tbody.innerHTML = '<tr><td colspan="6">Профіль викладача не знайдено</td></tr>';
                    return;
                }
            } else {
                // ADMIN and MANAGER see all students
                response = await apiClient.getStudents();
            }

            if (response?.success && Array.isArray(response.data)) {
                this.allStudents = response.data; // Store all students for filtering
                this.renderStudentsTable(response.data);
                this.loadGroupsForStudentFilter(); // Load groups for filter
            } else {
                tbody.innerHTML = '<tr><td colspan="6">Помилка завантаження студентів</td></tr>';
            }
        } catch (error) {
            console.error('Помилка завантаження студентів:', error);
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

    async loadStudentFilters() {
        // Load subjects for filter (only for teachers - their subjects, for others - all)
        try {
            let subjectResponse;
            if (this.currentUser?.role === 'TEACHER' && this.currentUser.teacherId) {
                subjectResponse = await apiClient.getSubjectsByTeacher(this.currentUser.teacherId);
            } else {
                subjectResponse = await apiClient.getPublicSubjects();
            }

            const subjectSelect = document.getElementById('student-filter-subject');
            if (subjectSelect && subjectResponse?.success) {
                const subjects = subjectResponse.data || [];
                subjectSelect.innerHTML = '<option value="">Всі дисципліни</option>' +
                    subjects.map(subject => 
                        `<option value="${subject.id}">${subject.subjectName || 'Без назви'}</option>`
                    ).join('');
            }
        } catch (error) {
            console.error('Error loading subjects filter:', error);
        }

        // Load groups for filter
        try {
            const groupResponse = await apiClient.getActiveGroups();
            const groupSelect = document.getElementById('student-filter-group');
            if (groupSelect && groupResponse?.success) {
                const groups = groupResponse.data || [];
                groupSelect.innerHTML = '<option value="">Всі групи</option>' +
                    groups.map(group => 
                        `<option value="${group.id}">${group.groupName || 'Без назви'}</option>`
                    ).join('');
            }
        } catch (error) {
            console.error('Error loading groups filter:', error);
        }

        // Add event listeners for filters
        const filterButton = document.getElementById('filter-students');
        if (filterButton) {
            filterButton.addEventListener('click', () => this.filterStudents());
        }

        const searchInput = document.getElementById('student-search');
        if (searchInput) {
            searchInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.filterStudents();
                }
            });
        }
    }

    async filterStudents() {
        const subjectFilter = document.getElementById('student-filter-subject')?.value || '';
        const groupFilter = document.getElementById('student-filter-group')?.value || '';
        const searchTerm = document.getElementById('student-search')?.value?.toLowerCase() || '';

        const tbody = document.getElementById('students-tbody');
        if (!tbody) return;

        // Get all student rows
        const rows = tbody.querySelectorAll('tr');
        
        rows.forEach(row => {
            const cells = row.querySelectorAll('td');
            if (cells.length < 6) return; // Skip if not enough cells

            const studentName = cells[0]?.textContent?.toLowerCase() || '';
            const studentEmail = cells[1]?.textContent?.toLowerCase() || '';
            const studentGroup = cells[2]?.textContent || '';
            
            // Apply filters
            let showRow = true;

            // Search filter (name or email)
            if (searchTerm && !studentName.includes(searchTerm) && !studentEmail.includes(searchTerm)) {
                showRow = false;
            }

            // Group filter
            if (groupFilter && !studentGroup.includes(groupFilter)) {
                showRow = false;
            }

            // Subject filter is already handled by loadStudentsData for teachers
            // For others, we would need additional API endpoint to filter by subject

            row.style.display = showRow ? '' : 'none';
        });
    }

    // === GROUPS MANAGEMENT ===
    async loadGroupsData() {
        const tbody = document.getElementById('groups-tbody');
        if (!tbody) return;

        tbody.innerHTML = '<tr><td colspan="8"><div class="loading"></div></td></tr>';

        try {
            const response = await apiClient.getGroups();
            if (response?.success && Array.isArray(response.data)) {
                this.renderGroupsTable(response.data);
            } else {
                tbody.innerHTML = '<tr><td colspan="8">Помилка завантаження груп</td></tr>';
            }
        } catch (error) {
            console.error('Помилка завантаження груп:', error);
            tbody.innerHTML = '<tr><td colspan="8">Помилка завантаження даних</td></tr>';
        }
    }

    renderGroupsTable(groups) {
        const tbody = document.getElementById('groups-tbody');
        if (!tbody) return;

        if (!groups.length) {
            tbody.innerHTML = '<tr><td colspan="8">Групи не знайдені</td></tr>';
            return;
        }

        tbody.innerHTML = groups.map(group => {
            const groupName = group.groupName || 'N/A';
            const groupCode = group.groupCode || 'N/A';
            const educationLevel = this.translateEducationLevel(group.educationLevel) || 'N/A';
            const courseYear = group.courseYear || 'N/A';
            const studyForm = this.translateStudyForm(group.studyForm) || 'N/A';
            const studentCount = group.currentStudentCount || 0;
            const enrollmentYear = group.enrollmentYear || group.startYear || 'N/A';
            
            // Check role permissions for action buttons - only MANAGER can edit/delete groups
            const role = this.currentUser?.role;
            const canEdit = role === 'MANAGER';
            const canDelete = role === 'ADMIN' || role === 'MANAGER'; // Both ADMIN and MANAGER can delete
            
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
                <td>${educationLevel}</td>
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
        try {
            const [groupResponse, studentsResponse] = await Promise.all([
                apiClient.getGroupById(groupId),
                apiClient.getGroupStudents(groupId)
            ]);
            
            if (groupResponse?.success) {
                const group = groupResponse.data;
                const students = studentsResponse?.success ? studentsResponse.data : [];
                
                // Add students to group object
                group.students = students;
                group.currentStudentCount = students.length;
                
                this.showGroupStudentsModal(group);
            } else {
                alert('Помилка завантаження групи');
            }
        } catch (error) {
            console.error('Error loading group students:', error);
            alert('Помилка: ' + error.message);
        }
    }

    showGroupStudentsModal(group) {
        // Store current group ID for operations
        this.currentGroupId = group.id;
        
        const modalHtml = `
            <div id="groupStudentsModal" class="modal">
                <div class="modal-content modal-wide">
                    <span class="close" onclick="dashboard.closeGroupStudentsModal()">&times;</span>
                    <h2>👥 Студенти групи: ${group.groupName}</h2>
                    <div class="group-info">
                        <div class="info-row">
                            <span><strong>🏷️ Код групи:</strong> ${group.groupCode}</span>
                            <span><strong>📖 Курс:</strong> ${group.courseYear}</span>
                            <span><strong>🎓 Рівень освіти:</strong> ${this.translateEducationLevel(group.educationLevel)}</span>
                            <span><strong>📚 Форма навчання:</strong> ${this.translateStudyForm(group.studyForm)}</span>
                        </div>
                        <div class="info-row">
                            <span><strong>🎓 Кількість студентів:</strong> ${group.currentStudentCount || 0}</span>
                            <span><strong>📅 Рік вступу:</strong> ${group.enrollmentYear || 'N/A'}</span>
                        </div>
                    </div>
                    
                    <div class="students-actions">
                        ${this.currentUser?.role === 'MANAGER' ? `
                            <button class="btn btn-success" onclick="dashboard.addStudentToGroup(${group.id})">➕ Додати студента</button>
                            <button class="btn btn-warning" onclick="dashboard.removeAllStudentsFromGroup(${group.id})" style="margin-left: 10px;">👥➖ Видалити всіх</button>
                        ` : ''}
                    </div>
                    
                    <div class="students-container">
                        <div class="table-container scrollable">
                            <table>
                                <thead>
                                    <tr>
                                        <th>🎓 Ім'я</th>
                                        <th>📧 Email</th>
                                        <th>📖 Курс</th>
                                        <th>⭐ Середній бал</th>
                                        <th>⚙️ Дії</th>
                                    </tr>
                                </thead>
                                <tbody id="group-students-tbody">
                                    ${this.renderGroupStudentsTable(group.students || [])}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        // Remove existing modal if any
        const existingModal = document.getElementById('groupStudentsModal');
        if (existingModal) {
            existingModal.remove();
        }
        
        // Add new modal
        document.body.insertAdjacentHTML('beforeend', modalHtml);
        
        // Show modal
        const modal = document.getElementById('groupStudentsModal');
        modal.style.display = 'block';
        
        // Handle modal events
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                this.closeGroupStudentsModal();
            }
        });
        
        // Prevent body scroll when modal is open
        document.body.style.overflow = 'hidden';
    }

    renderGroupStudentsTable(students) {
        if (!students || !students.length) {
            return '<tr><td colspan="5">Студенти в групі не знайдені</td></tr>';
        }

        return students.map(student => {
            const fullName = student.user ? `${student.user.firstName} ${student.user.lastName}` : (student.fullName || 'N/A');
            const email = student.user ? student.user.email : 'N/A';
            const course = student.course || 'N/A';
            const averageGrade = student.averageGrade !== undefined ? 
                (student.averageGrade > 0 ? student.averageGrade.toFixed(2) : '0.00') : 'N/A';
            
            const canEdit = this.currentUser?.role === 'MANAGER';
            
            return `
                <tr>
                    <td>${fullName}</td>
                    <td>${email}</td>
                    <td>${course}</td>
                    <td>${averageGrade}</td>
                    <td>
                        <div class="table-actions">
                            <button class="btn btn-sm btn-info" onclick="dashboard.viewStudentInfo(${student.id})">ℹ️ Інфо</button>
                            <button class="btn btn-sm btn-primary" onclick="dashboard.viewStudentGrades(${student.id})">📝 Оцінки</button>
                            ${canEdit ? `
                                <button class="btn btn-sm btn-danger" onclick="dashboard.removeStudentFromGroup(${student.id})">🗑️ Видалити</button>
                            ` : ''}
                        </div>
                    </td>
                </tr>
            `;
        }).join('');
    }

    closeGroupStudentsModal() {
        const modal = document.getElementById('groupStudentsModal');
        if (modal) {
            modal.remove();
        }
        // Clear current group ID
        this.currentGroupId = null;
        // Restore body scroll
        document.body.style.overflow = 'auto';
    }

    async addStudentToGroup(groupId) {
        try {
            // Get students without group
            const studentsResponse = await apiClient.getStudentsWithoutGroup();
            if (!studentsResponse?.success) {
                alert('Помилка завантаження студентів');
                return;
            }

            const availableStudents = studentsResponse.data;

            if (!availableStudents.length) {
                alert('Немає доступних студентів для додавання до групи (всі студенти вже призначені до груп)');
                return;
            }

            this.showAddStudentToGroupModal(groupId, availableStudents);
        } catch (error) {
            console.error('Error loading students:', error);
            alert('Помилка: ' + error.message);
        }
    }

    showAddStudentToGroupModal(groupId, students) {
        const modalHtml = `
            <div id="addStudentModal" class="modal">
                <div class="modal-content">
                    <span class="close" onclick="dashboard.closeAddStudentModal()">&times;</span>
                    <h2>➕ Додати студента до групи</h2>
                    
                    <form id="add-student-form">
                        <div class="form-group">
                            <label for="student-search-input">🔍 Пошук студента:</label>
                            <input type="text" id="student-search-input" class="form-control" 
                                   placeholder="Введіть ім'я або прізвище студента..." />
                        </div>
                        
                        <div class="form-group">
                            <label for="student-select">Оберіть студента:</label>
                            <select id="student-select" name="studentId" required>
                                <option value="">-- Оберіть студента --</option>
                                ${students.map(student => {
                                    const firstName = student.user ? student.user.firstName : student.firstName;
                                    const lastName = student.user ? student.user.lastName : student.lastName;
                                    const email = student.user ? student.user.email : student.email;
                                    return `
                                        <option value="${student.id}" data-name="${firstName} ${lastName}" data-email="${email}">
                                            ${firstName} ${lastName} (${email})
                                        </option>
                                    `;
                                }).join('')}
                            </select>
                        </div>
                        
                        <div class="form-actions">
                            <button type="submit" class="btn btn-success">➕ Додати</button>
                            <button type="button" class="btn btn-secondary" onclick="dashboard.closeAddStudentModal()">❌ Скасувати</button>
                        </div>
                    </form>
                </div>
            </div>
        `;
        
        // Remove existing modal if any
        const existingModal = document.getElementById('addStudentModal');
        if (existingModal) {
            existingModal.remove();
        }
        
        // Add new modal
        document.body.insertAdjacentHTML('beforeend', modalHtml);
        
        // Add search functionality
        const searchInput = document.getElementById('student-search-input');
        const studentSelect = document.getElementById('student-select');
        
        if (searchInput && studentSelect) {
            let searchTimeout;
            searchInput.addEventListener('input', async (e) => {
                const query = e.target.value.trim();
                
                // Clear previous timeout
                if (searchTimeout) {
                    clearTimeout(searchTimeout);
                }
                
                // Debounce search requests
                searchTimeout = setTimeout(async () => {
                    if (query.length >= 2) {
                        try {
                            const searchResponse = await apiClient.searchStudentsWithoutGroup(query);
                            if (searchResponse?.success) {
                                // Update select options with search results
                                const filteredStudents = searchResponse.data;
                                updateStudentSelectOptions(studentSelect, filteredStudents);
                            }
                        } catch (error) {
                            console.error('Search error:', error);
                        }
                    } else if (query.length === 0) {
                        // Reset to all available students if search is cleared
                        updateStudentSelectOptions(studentSelect, students);
                    }
                }, 300);
            });
        }
        
        // Helper function to update select options
        function updateStudentSelectOptions(selectElement, studentList) {
            const currentValue = selectElement.value;
            selectElement.innerHTML = '<option value="">-- Оберіть студента --</option>';
            
            studentList.forEach(student => {
                const firstName = student.user ? student.user.firstName : student.firstName;
                const lastName = student.user ? student.user.lastName : student.lastName;
                const email = student.user ? student.user.email : student.email;
                
                const option = document.createElement('option');
                option.value = student.id;
                option.textContent = `${firstName} ${lastName} (${email})`;
                option.dataset.name = `${firstName} ${lastName}`;
                option.dataset.email = email;
                
                selectElement.appendChild(option);
            });
            
            // Restore selected value if it still exists
            if (currentValue) {
                selectElement.value = currentValue;
            }
        }
        
        // Handle form submission
        document.getElementById('add-student-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const studentId = parseInt(formData.get('studentId'));
            
            if (!studentId) {
                alert('Будь ласка, оберіть студента');
                return;
            }

            try {
                const response = await apiClient.addStudentToGroup(groupId, studentId);
                if (response?.success) {
                    alert('Студента додано до групи успішно!');
                    this.closeAddStudentModal();
                    
                    // Refresh both the group students modal and the main groups table
                    await this.refreshGroupData(groupId);
                    
                } else {
                    throw new Error(response?.error || 'Невідома помилка');
                }
            } catch (error) {
                console.error('Error adding student to group:', error);
                alert('Помилка додавання студента до групи: ' + error.message);
            }
        });
        
        // Show modal
        const modal = document.getElementById('addStudentModal');
        modal.style.display = 'block';
        
        // Handle modal events
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                this.closeAddStudentModal();
            }
        });
    }

    closeAddStudentModal() {
        const modal = document.getElementById('addStudentModal');
        if (modal) {
            modal.remove();
        }
    }

    async removeStudentFromGroup(studentId) {
        if (confirm('Ви впевнені, що хочете видалити студента з групи?')) {
            try {
                // Find the group ID from the modal data or from the current group context
                const groupStudentsModal = document.getElementById('groupStudentsModal');
                if (!groupStudentsModal) {
                    alert('Помилка: не знайдено контекст групи');
                    return;
                }
                
                let groupId = this.currentGroupId;
                if (!groupId) {
                    alert('Помилка: не вдалося визначити групу');
                    return;
                }

                console.log('Removing student', studentId, 'from group', groupId);
                
                const response = await apiClient.removeStudentFromGroup(groupId, studentId);
                if (response?.success) {
                    alert('Студента видалено з групи успішно!');
                    
                    // Refresh both the group students modal and the main groups table
                    await this.refreshGroupData(groupId);
                    
                } else {
                    throw new Error(response?.error || 'Невідома помилка');
                }
            } catch (error) {
                console.error('Error removing student from group:', error);
                alert('Помилка видалення студента з групи: ' + error.message);
            }
        }
    }

    async refreshGroupData(groupId) {
        try {
            // Refresh the group students modal if it's open
            const [groupResponse, studentsResponse] = await Promise.all([
                apiClient.getGroupById(groupId),
                apiClient.getGroupStudents(groupId)
            ]);
            
            if (groupResponse?.success) {
                const group = groupResponse.data;
                const students = studentsResponse?.success ? studentsResponse.data : [];
                
                group.students = students;
                group.currentStudentCount = students.length;
                
                // Update the modal if it's open
                const modal = document.getElementById('groupStudentsModal');
                if (modal) {
                    this.showGroupStudentsModal(group);
                }
                
                // Refresh the main groups table to show updated student count
                await this.loadGroupsData();
            }
        } catch (error) {
            console.error('Error refreshing group data:', error);
        }
    }

    async viewStudentInfo(studentId) {
        try {
            const response = await apiClient.getStudent(studentId);
            if (response?.success || response) {
                const student = response.data || response;
                this.showStudentInfoModal(student);
            } else {
                alert('Помилка завантаження інформації про студента');
            }
        } catch (error) {
            console.error('Error loading student info:', error);
            alert('Помилка: ' + error.message);
        }
    }

    showStudentInfoModal(student) {
        const modalHtml = `
            <div id="studentInfoModal" class="modal">
                <div class="modal-content">
                    <span class="close" onclick="dashboard.closeStudentInfoModal()">&times;</span>
                    <h2>ℹ️ Інформація про студента</h2>
                    <div class="student-info">
                        <div class="info-section">
                            <h3>👤 Особисті дані</h3>
                            <p><strong>Ім'я:</strong> ${student.user?.firstName || 'N/A'}</p>
                            <p><strong>Прізвище:</strong> ${student.user?.lastName || 'N/A'}</p>
                            <p><strong>Email:</strong> ${student.user?.email || 'N/A'}</p>
                            <p><strong>Статус:</strong> ${student.user?.isActive ? 'Активний' : 'Неактивний'}</p>
                        </div>
                        
                        <div class="info-section">
                            <h3>🎓 Навчальна інформація</h3>
                            <p><strong>Група:</strong> ${student.group?.groupName || 'Не призначено'}</p>
                            <p><strong>Курс:</strong> ${student.course || 'N/A'}</p>
                            <p><strong>Середній бал:</strong> ${student.averageGrade ? student.averageGrade.toFixed(2) : 'N/A'}</p>
                            <p><strong>Дата створення:</strong> ${student.user?.createdAt ? new Date(student.user.createdAt).toLocaleDateString('uk-UA') : 'N/A'}</p>
                        </div>
                    </div>
                    
                    <div class="modal-actions">
                        <button class="btn btn-primary" onclick="dashboard.viewStudentGrades(${student.id})">📝 Переглянути оцінки</button>
                        <button class="btn btn-secondary" onclick="dashboard.closeStudentInfoModal()">❌ Закрити</button>
                    </div>
                </div>
            </div>
        `;
        
        // Remove existing modal if any
        const existingModal = document.getElementById('studentInfoModal');
        if (existingModal) {
            existingModal.remove();
        }
        
        // Add new modal
        document.body.insertAdjacentHTML('beforeend', modalHtml);
        
        // Show modal
        const modal = document.getElementById('studentInfoModal');
        modal.style.display = 'block';
        
        // Handle modal events
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                this.closeStudentInfoModal();
            }
        });
    }

    closeStudentInfoModal() {
        const modal = document.getElementById('studentInfoModal');
        if (modal) {
            modal.remove();
        }
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
        const table = document.getElementById('subjects-table');
        if (!tbody || !table) return;

        // Update table header based on user role
        const thead = table.querySelector('thead tr');
        if (thead) {
            const isGuest = this.currentUser?.role === 'GUEST';
            const colCount = isGuest ? 4 : 5;
            
            thead.innerHTML = `
                <th>📚 Назва</th>
                <th>👨‍🏫 Викладач</th>
                <th>💳 Кредити</th>
                <th>📅 Семестр</th>
                ${!isGuest ? '<th>⚙️ Дії</th>' : ''}
            `;
        }

        if (!subjects.length) {
            const colCount = this.currentUser?.role === 'GUEST' ? 4 : 5;
            tbody.innerHTML = `<tr><td colspan="${colCount}">Дисципліни не знайдені</td></tr>`;
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
            
            const isGuest = this.currentUser?.role === 'GUEST';
            
            return `
            <tr>
                <td>${subjectName}</td>
                <td>${teacherName}</td>
                <td>${credits}</td>
                <td>${semester}</td>
                ${!isGuest ? `
                <td>
                    <div class="table-actions">
                        <button class="btn btn-sm btn-primary" onclick="dashboard.viewSubject(${subject.id})">Переглянути</button>
                        <button class="btn btn-sm btn-warning" onclick="dashboard.editSubject(${subject.id})">Редагувати</button>
                        <button class="btn btn-sm btn-danger" onclick="dashboard.deleteSubject(${subject.id})">Видалити</button>
                    </div>
                </td>
                ` : ''}
            </tr>
        `;
        }).join('');
    }    // Modal methods
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
            let response;
            
            // Role-based student loading for grades
            if (this.currentUser?.role === 'TEACHER') {
                // Teachers see only students from their subjects
                if (this.currentUser.teacherId) {
                    response = await apiClient.getStudentsByTeacher(this.currentUser.teacherId);
                } else {
                    console.error('Teacher profile not found');
                    return;
                }
            } else {
                // ADMIN and MANAGER see all students
                response = await apiClient.getUsersByRole('STUDENT');
            }

            const select = document.getElementById(selectId);
            if (select && response?.success) {
                const students = response.data || [];
                select.innerHTML = '<option value="">Оберіть студента...</option>' +
                    students.map(student => {
                        // Handle different response formats
                        const studentId = student.id || student.userId;
                        const firstName = student.firstName || student.user?.firstName;
                        const lastName = student.lastName || student.user?.lastName;
                        return `<option value="${studentId}" ${selectedId == studentId ? 'selected' : ''}>
                            ${firstName} ${lastName}
                        </option>`;
                    }).join('');
            }
        } catch (error) {
            console.error('Error loading students:', error);
        }
    }

    async loadSubjectsForGrades(selectId = 'grade-subject-select', selectedId = null) {
        try {
            let response;
            
            // Role-based subject loading for grades
            if (this.currentUser?.role === 'TEACHER') {
                // Teachers see only subjects they teach
                if (this.currentUser.teacherId) {
                    response = await apiClient.getSubjectsByTeacher(this.currentUser.teacherId);
                } else {
                    console.error('Teacher profile not found');
                    return;
                }
            } else {
                // ADMIN and MANAGER see all subjects
                response = await apiClient.getPublicSubjects();
            }
            
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
    debounceUserSearch() {
        // Clear previous timeout
        if (this.userSearchTimeout) {
            clearTimeout(this.userSearchTimeout);
        }
        
        // Set new timeout for debounced search
        this.userSearchTimeout = setTimeout(() => {
            this.searchUsers();
        }, 300); // 300ms delay
    }

    debounceTeacherSearch() {
        // Clear previous timeout
        if (this.teacherSearchTimeout) {
            clearTimeout(this.teacherSearchTimeout);
        }
        
        // Set new timeout for debounced search
        this.teacherSearchTimeout = setTimeout(() => {
            this.searchTeachers();
        }, 300); // 300ms delay
    }

    debounceStudentSearch() {
        // Clear previous timeout
        if (this.studentSearchTimeout) {
            clearTimeout(this.studentSearchTimeout);
        }
        
        // Set new timeout for debounced search
        this.studentSearchTimeout = setTimeout(() => {
            this.searchStudents();
        }, 300); // 300ms delay
    }

    debounceGroupSearch() {
        // Clear previous timeout
        if (this.groupSearchTimeout) {
            clearTimeout(this.groupSearchTimeout);
        }
        
        // Set new timeout for debounced search
        this.groupSearchTimeout = setTimeout(() => {
            this.searchGroups();
        }, 300); // 300ms delay
    }

    debounceSubjectSearch() {
        // Clear previous timeout
        if (this.subjectSearchTimeout) {
            clearTimeout(this.subjectSearchTimeout);
        }
        
        // Set new timeout for debounced search
        this.subjectSearchTimeout = setTimeout(() => {
            this.searchSubjects();
        }, 300); // 300ms delay
    }

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

    async viewStudentGrades(studentId) {
        try {
            // Get student grades
            const response = await apiClient.getGradesByStudent(studentId);
            const grades = response?.success ? response.data : (Array.isArray(response) ? response : []);
            
            // Get student info
            const studentResponse = await apiClient.getStudent(studentId);
            const student = studentResponse?.success ? studentResponse.data : studentResponse;
            
            this.showStudentGradesModal(student, grades);
        } catch (error) {
            console.error('Error loading student grades:', error);
            alert('Помилка завантаження оцінок студента');
        }
    }

    showStudentGradesModal(student, grades) {
        const modalHtml = `
            <div id="studentGradesModal" class="modal">
                <div class="modal-content">
                    <span class="close" onclick="dashboard.closeStudentGradesModal()">&times;</span>
                    <h2>Оцінки студента: ${student?.user?.firstName || ''} ${student?.user?.lastName || ''}</h2>
                    <div class="grades-container">
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr>
                                        <th>📚 Дисципліна</th>
                                        <th>📋 Тип</th>
                                        <th>⭐ Оцінка</th>
                                        <th>📅 Дата</th>
                                        ${this.currentUser?.role === 'TEACHER' ? '<th>⚙️ Дії</th>' : ''}
                                    </tr>
                                </thead>
                                <tbody>
                                    ${grades.length ? grades.map(grade => `
                                        <tr>
                                            <td>${grade.subjectName || 'N/A'}</td>
                                            <td>${this.translateGradeType(grade.gradeType) || 'N/A'}</td>
                                            <td><strong>${grade.gradeValue || 'N/A'}</strong></td>
                                            <td>${grade.gradeDate ? new Date(grade.gradeDate).toLocaleDateString('uk-UA') : 'N/A'}</td>
                                            ${this.currentUser?.role === 'TEACHER' ? `
                                                <td>
                                                    ${this.canTeacherEditGrade(grade) ? `
                                                        <button class="btn btn-sm btn-primary" onclick="dashboard.editGrade(${grade.id})">Редагувати</button>
                                                    ` : '<span class="text-muted">—</span>'}
                                                </td>
                                            ` : ''}
                                        </tr>
                                    `).join('') : `
                                        <tr><td colspan="${this.currentUser?.role === 'TEACHER' ? '5' : '4'}">Оцінки не знайдені</td></tr>
                                    `}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        // Remove existing modal if any
        const existingModal = document.getElementById('studentGradesModal');
        if (existingModal) {
            existingModal.remove();
        }
        
        // Add new modal
        document.body.insertAdjacentHTML('beforeend', modalHtml);
        
        // Show modal and add scroll event handling
        const modal = document.getElementById('studentGradesModal');
        modal.style.display = 'block';
        
        // Allow closing modal by clicking outside
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                this.closeStudentGradesModal();
            }
        });
        
        // Allow closing modal with Escape key
        const escapeHandler = (e) => {
            if (e.key === 'Escape') {
                this.closeStudentGradesModal();
                document.removeEventListener('keydown', escapeHandler);
            }
        };
        document.addEventListener('keydown', escapeHandler);
        
        // Prevent body scroll when modal is open
        document.body.style.overflow = 'hidden';
    }

    canTeacherEditGrade(grade) {
        // Teacher can edit grade only if they teach this subject
        return this.currentUser?.role === 'TEACHER' && 
               grade.teacherId === this.currentUser.teacherId;
    }

    closeStudentGradesModal() {
        const modal = document.getElementById('studentGradesModal');
        if (modal) {
            modal.remove();
        }
        // Restore body scroll
        document.body.style.overflow = 'auto';
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
                const subjectId = response?.data?.id || response?.id || this.currentSubject?.id;
                
                // Assign teacher if selected
                if (teacherId && subjectId) {
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
        
        try {
            const response = await apiClient.searchPublicStudents(searchTerm);
            if (response?.success && Array.isArray(response.data)) {
                this.renderStudentsTable(response.data);
            }
        } catch (error) {
            console.error('Error searching students:', error);
            await this.loadStudentsData();
        }
    }

    // Filter students based on current filter values
    filterStudents() {
        const groupFilter = document.getElementById('student-filter-group')?.value || '';
        const gradeMin = parseFloat(document.getElementById('student-filter-grade-min')?.value) || 0;
        const gradeMax = parseFloat(document.getElementById('student-filter-grade-max')?.value) || 100;

        let filteredStudents = [...this.allStudents];

        // Filter by group
        if (groupFilter) {
            filteredStudents = filteredStudents.filter(student => 
                student.group && student.group.id && student.group.id.toString() === groupFilter
            );
        }

        // Filter by average grade
        filteredStudents = filteredStudents.filter(student => {
            const avgGrade = student.averageGrade || 0;
            return avgGrade >= gradeMin && avgGrade <= gradeMax;
        });

        this.renderStudentsTable(filteredStudents);
    }

    // Sort and filter students
    sortAndFilterStudents() {
        const sortBy = document.getElementById('student-sort')?.value || 'name-asc';
        
        // First apply filters
        this.filterStudents();
        
        // Get filtered students from the table (or refilter)
        const groupFilter = document.getElementById('student-filter-group')?.value || '';
        const gradeMin = parseFloat(document.getElementById('student-filter-grade-min')?.value) || 0;
        const gradeMax = parseFloat(document.getElementById('student-filter-grade-max')?.value) || 100;

        let filteredStudents = [...this.allStudents];

        // Apply filters
        if (groupFilter) {
            filteredStudents = filteredStudents.filter(student => 
                student.group && student.group.id && student.group.id.toString() === groupFilter
            );
        }

        filteredStudents = filteredStudents.filter(student => {
            const avgGrade = student.averageGrade || 0;
            return avgGrade >= gradeMin && avgGrade <= gradeMax;
        });

        // Apply sorting
        filteredStudents.sort((a, b) => {
            switch (sortBy) {
                case 'name-asc':
                    const nameA = a.user ? `${a.user.lastName} ${a.user.firstName}` : (a.fullName || '');
                    const nameB = b.user ? `${b.user.lastName} ${b.user.firstName}` : (b.fullName || '');
                    return nameA.localeCompare(nameB);
                case 'name-desc':
                    const nameA2 = a.user ? `${a.user.lastName} ${a.user.firstName}` : (a.fullName || '');
                    const nameB2 = b.user ? `${b.user.lastName} ${b.user.firstName}` : (b.fullName || '');
                    return nameB2.localeCompare(nameA2);
                case 'grade-desc':
                    return (b.averageGrade || 0) - (a.averageGrade || 0);
                case 'grade-asc':
                    return (a.averageGrade || 0) - (b.averageGrade || 0);
                default:
                    return 0;
            }
        });

        this.renderStudentsTable(filteredStudents);
    }

    async searchGroups() {
        await this.filterGroups();
    }

    async filterGroups() {
        const searchInput = document.getElementById('group-search');
        const educationLevelFilter = document.getElementById('education-level-filter');
        const courseFilter = document.getElementById('course-filter');
        const studyFormFilter = document.getElementById('study-form-filter');
        const enrollmentYearFilter = document.getElementById('enrollment-year-filter');
        
        const searchTerm = searchInput?.value?.trim();
        const educationLevel = educationLevelFilter?.value;
        const course = courseFilter?.value;
        const studyForm = studyFormFilter?.value;
        const enrollmentYear = enrollmentYearFilter?.value;
        
        // If no filters applied, load all groups
        if (!searchTerm && !educationLevel && !course && !studyForm && !enrollmentYear) {
            await this.loadGroupsData();
            return;
        }
        
        try {
            let groups = [];
            
            // If search term is provided, use search API
            if (searchTerm) {
                const response = await apiClient.searchGroups(searchTerm);
                groups = response?.success ? response.data : [];
            } else {
                // Otherwise load all groups for filtering
                const response = await apiClient.getGroups();
                groups = response?.success ? response.data : [];
            }
            
            // Apply client-side filters
            let filteredGroups = groups.filter(group => {
                let matches = true;
                
                // Education level filter
                if (educationLevel && group.educationLevel !== educationLevel) {
                    matches = false;
                }
                
                // Course filter
                if (course && group.courseYear !== parseInt(course)) {
                    matches = false;
                }
                
                // Study form filter
                if (studyForm && group.studyForm !== studyForm) {
                    matches = false;
                }
                
                // Enrollment year filter
                if (enrollmentYear && group.enrollmentYear !== parseInt(enrollmentYear)) {
                    matches = false;
                }
                
                return matches;
            });
            
            this.renderGroupsTable(filteredGroups);
        } catch (error) {
            console.error('Помилка фільтрації груп:', error);
        }
    }

    // === GROUP MODAL METHODS ===
    showAddGroupModal() {
        const role = this.currentUser?.role;
        if (role !== 'MANAGER') {
            alert('У вас немає прав для створення груп');
            return;
        }
        
        this.showGroupModal();
    }

    async showGroupModal(group = null) {
        const modal = document.getElementById('modal');
        const title = document.getElementById('modal-title');
        const body = document.getElementById('modal-body');

        title.textContent = group ? 'Редагувати групу' : 'Додати групу';
        
        body.innerHTML = `
            <form id="group-form" class="group-form">
                <div class="form-row">
                    <div class="form-group">
                        <label for="group-name">🏷️ Назва групи:</label>
                        <input type="text" id="group-name" name="groupName" required 
                               placeholder="Наприклад: КН-21" 
                               value="${group?.groupName || ''}" 
                               maxlength="20">
                    </div>
                    <div class="form-group">
                        <label for="group-code">🏷️ Код групи:</label>
                        <input type="text" id="group-code" name="groupCode" required 
                               placeholder="Наприклад: КН21" 
                               value="${group?.groupCode || ''}" 
                               maxlength="10">
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="group-education-level">🎓 Рівень освіти:</label>
                        <select id="group-education-level" name="educationLevel" required>
                            <option value="">Оберіть рівень освіти</option>
                            <option value="BACHELOR" ${group?.educationLevel === 'BACHELOR' ? 'selected' : ''}>🎓 Бакалавр (1-5 курс)</option>
                            <option value="MASTER" ${group?.educationLevel === 'MASTER' ? 'selected' : ''}>🎯 Магістр (1-2 курс)</option>
                            <option value="PHD" ${group?.educationLevel === 'PHD' ? 'selected' : ''}>👨‍🔬 Аспірант (1-4 курс)</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="group-course">📖 Курс:</label>
                        <select id="group-course" name="courseYear" required>
                            <option value="">Спочатку оберіть рівень освіти</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="group-study-form">📚 Форма навчання:</label>
                        <select id="group-study-form" name="studyForm" required>
                            <option value="">Оберіть форму навчання</option>
                            <option value="FULL_TIME" ${group?.studyForm === 'FULL_TIME' ? 'selected' : ''}>🎓 Денна</option>
                            <option value="EVENING" ${group?.studyForm === 'EVENING' ? 'selected' : ''}>🌙 Вечірня</option>
                            <option value="PART_TIME" ${group?.studyForm === 'PART_TIME' ? 'selected' : ''}>📮 Заочна</option>
                            <option value="DISTANCE" ${group?.studyForm === 'DISTANCE' ? 'selected' : ''}>💻 Дистанційна</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="group-enrollment-year">📅 Рік вступу:</label>
                        <input type="number" id="group-enrollment-year" name="enrollmentYear" 
                               min="2020" max="2030" 
                               value="${group?.enrollmentYear || new Date().getFullYear()}" 
                               required>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="group-max-students">👥 Максимум студентів (необов'язково):</label>
                        <input type="number" id="group-max-students" name="maxStudents" 
                               min="1" max="50" 
                               value="${group?.maxStudents || ''}" 
                               placeholder="Наприклад: 25">
                    </div>
                    <div class="form-group">
                        <label for="group-specialization">📚 Спеціалізація (необов'язково):</label>
                        <input type="text" id="group-specialization" name="specialization" 
                               value="${group?.specialization || ''}" 
                               placeholder="Наприклад: Комп'ютерні науки" 
                               maxlength="200">
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="group-students">👥 Студенти групи:</label>
                    <div class="students-selection" id="students-selection">
                        <div class="loading">Завантаження студентів...</div>
                    </div>
                </div>
                
                ${group?.currentStudentCount ? `
                <div class="form-group">
                    <div class="info-display">
                        <strong>🎓 Поточна кількість студентів:</strong> ${group.currentStudentCount}
                    </div>
                </div>
                ` : ''}
                
                <div class="form-actions">
                    <button type="submit" class="btn btn-success">💾 ${group ? 'Оновити' : 'Створити'} групу</button>
                    <button type="button" class="btn btn-secondary" onclick="document.getElementById('modal').style.display='none'">❌ Скасувати</button>
                </div>
            </form>
        `;

        // Load available students
        await this.loadStudentsForGroup(group);

        // Setup education level change handler
        this.setupEducationLevelHandler(group);

        // Handle form submission
        document.getElementById('group-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            await this.handleGroupSubmit(group);
        });

        modal.style.display = 'block';
    }

    setupEducationLevelHandler(group = null) {
        const educationLevelSelect = document.getElementById('group-education-level');
        const courseSelect = document.getElementById('group-course');

        if (!educationLevelSelect || !courseSelect) return;

        // Education level course mapping
        const courseMappings = {
            'BACHELOR': { min: 1, max: 5, label: 'Бакалавр' },
            'MASTER': { min: 1, max: 2, label: 'Магістр' },
            'PHD': { min: 1, max: 4, label: 'Аспірант' }
        };

        const updateCourseOptions = (educationLevel) => {
            courseSelect.innerHTML = '<option value="">Оберіть курс</option>';
            
            if (educationLevel && courseMappings[educationLevel]) {
                const mapping = courseMappings[educationLevel];
                for (let i = mapping.min; i <= mapping.max; i++) {
                    const option = document.createElement('option');
                    option.value = i;
                    option.textContent = `${i} курс`;
                    // Preserve selected course if editing existing group
                    if (group?.courseYear === i && group?.educationLevel === educationLevel) {
                        option.selected = true;
                    }
                    courseSelect.appendChild(option);
                }
            }
        };

        // Handle education level change
        educationLevelSelect.addEventListener('change', (e) => {
            updateCourseOptions(e.target.value);
        });

        // Initialize course options if editing existing group
        if (group?.educationLevel) {
            updateCourseOptions(group.educationLevel);
        }
    }

    async loadStudentsForGroup(group = null) {
        try {
            // Get students without group and students already in the current group
            const studentsWithoutGroupResponse = await apiClient.getStudentsWithoutGroup();
            const studentsWithoutGroup = studentsWithoutGroupResponse?.success ? studentsWithoutGroupResponse.data : [];
            
            // Get group students if editing existing group
            let groupStudents = [];
            if (group?.id) {
                const groupStudentsResponse = await apiClient.getGroupStudents(group.id);
                groupStudents = groupStudentsResponse?.success ? groupStudentsResponse.data : (group.students || []);
            }
            
            // Combine students without group and current group students (for editing)
            const allAvailableStudents = [...studentsWithoutGroup, ...groupStudents];
            
            // Remove duplicates by id
            const uniqueStudents = allAvailableStudents.filter((student, index, self) =>
                index === self.findIndex(s => s.id === student.id)
            );
            
            const groupStudentIds = groupStudents.map(s => s.id || s.userId);

            const container = document.getElementById('students-selection');
            if (!container) return;

            if (!uniqueStudents.length) {
                container.innerHTML = '<p class="no-data">Немає доступних студентів (всі студенти вже призначені до груп)</p>';
                return;
            }

            // Create checkable list of students
            container.innerHTML = `
                <div class="students-filter-container">
                    <input type="text" id="students-filter" class="students-filter" 
                           placeholder="🔍 Пошук студентів..." />
                </div>
                <div class="students-list" id="students-list">
                    ${uniqueStudents.map(student => {
                        const fullName = student.user ? `${student.user.firstName} ${student.user.lastName}` : 
                                        `${student.firstName} ${student.lastName}`;
                        const email = student.user ? student.user.email : student.email;
                        return `
                        <label class="student-item">
                            <input type="checkbox" 
                                   name="selectedStudents" 
                                   value="${student.id}"
                                   ${groupStudentIds.includes(student.id) ? 'checked' : ''}>
                            <span class="student-name">${fullName}</span>
                            <span class="student-email">${email}</span>
                        </label>
                    `;}).join('')}
                </div>
            `;

            // Add search functionality
            const filterInput = document.getElementById('students-filter');
            if (filterInput) {
                filterInput.addEventListener('input', (e) => {
                    const query = e.target.value.toLowerCase();
                    const studentItems = container.querySelectorAll('.student-item');
                    
                    studentItems.forEach(item => {
                        const name = item.querySelector('.student-name').textContent.toLowerCase();
                        const email = item.querySelector('.student-email').textContent.toLowerCase();
                        const matches = name.includes(query) || email.includes(query);
                        item.style.display = matches ? 'flex' : 'none';
                    });
                });
            }

        } catch (error) {
            console.error('Error loading students for group:', error);
            const container = document.getElementById('students-selection');
            if (container) {
                container.innerHTML = '<p class="error">Помилка завантаження студентів</p>';
            }
        }
    }

    async handleGroupSubmit(existingGroup = null) {
        const form = document.getElementById('group-form');
        const formData = new FormData(form);
        
        // Get selected students
        const selectedStudents = Array.from(form.querySelectorAll('input[name="selectedStudents"]:checked'))
            .map(checkbox => parseInt(checkbox.value));

        const groupData = {
            groupName: formData.get('groupName'),
            groupCode: formData.get('groupCode'),
            courseYear: parseInt(formData.get('courseYear')),
            educationLevel: formData.get('educationLevel'),
            studyForm: formData.get('studyForm'),
            enrollmentYear: parseInt(formData.get('enrollmentYear')),
            maxStudents: formData.get('maxStudents') ? parseInt(formData.get('maxStudents')) : null,
            specialization: formData.get('specialization') || null,
            isActive: true
        };

        try {
            let response;
            if (existingGroup) {
                // Update existing group
                response = await apiClient.updateGroup(existingGroup.id, groupData);
                if (response?.success) {
                    // Update student assignments
                    const studentsResponse = await apiClient.updateGroupStudents(existingGroup.id, selectedStudents);
                    if (studentsResponse?.success) {
                        alert('Групу та список студентів оновлено успішно!');
                    } else {
                        alert('Групу оновлено, але сталася помилка з оновленням студентів: ' + (studentsResponse?.error || 'Невідома помилка'));
                    }
                }
            } else {
                // Create new group
                response = await apiClient.createGroup(groupData);
                if (response?.success) {
                    const newGroupId = response.data.id;
                    // Assign selected students to the new group
                    if (selectedStudents.length > 0) {
                        const promises = selectedStudents.map(studentId => 
                            apiClient.addStudentToGroup(newGroupId, studentId)
                        );
                        const studentsResults = await Promise.all(promises);
                        const failed = studentsResults.filter(r => !r?.success);
                        
                        if (failed.length > 0) {
                            alert(`Групу створено, але ${failed.length} студентів не вдалося додати до групи`);
                        } else {
                            alert('Групу створено та студентів додано успішно!');
                        }
                    } else {
                        alert('Групу створено успішно!');
                    }
                }
            }

            if (response?.success) {
                document.getElementById('modal').style.display = 'none';
                this.loadGroupsData();
            } else {
                throw new Error(response?.data?.message || response?.error || 'Невідома помилка');
            }
        } catch (error) {
            console.error('Error saving group:', error);
            alert('Помилка збереження групи: ' + error.message);
        }
    }

    async editGroup(groupId) {
        try {
            const response = await apiClient.getGroupById(groupId);
            if (response?.success) {
                this.showGroupModal(response.data);
            } else {
                alert('Помилка завантаження групи');
            }
        } catch (error) {
            console.error('Error loading group for edit:', error);
            alert('Помилка: ' + error.message);
        }
    }

    async removeAllStudentsFromGroup(groupId) {
        try {
            // Get group info first
            const groupResponse = await apiClient.getGroupById(groupId);
            if (!groupResponse?.success) {
                throw new Error('Не вдалося отримати інформацію про групу');
            }

            const group = groupResponse.data;
            const studentCount = group.students?.length || 0;

            if (studentCount === 0) {
                alert('В цій групі немає студентів для видалення');
                return;
            }

            const confirmMessage = `Ви впевнені, що хочете видалити ВСІХ ${studentCount} студентів з групи "${group.groupName}"?\n\nЦя дія незворотна!`;
            
            if (!confirm(confirmMessage)) {
                return;
            }

            // Remove all students
            const promises = group.students.map(student => 
                apiClient.removeStudentFromGroup(groupId, student.id)
            );

            const results = await Promise.all(promises);
            const failed = results.filter(r => !r?.success);

            if (failed.length > 0) {
                alert(`Помилка: ${failed.length} студентів не вдалося видалити з групи`);
            } else {
                alert(`Всіх ${studentCount} студентів успішно видалено з групи!`);
            }

            // Refresh data
            await this.refreshGroupData(groupId);

        } catch (error) {
            console.error('Error removing all students from group:', error);
            alert('Помилка видалення студентів: ' + error.message);
        }
    }

    async deleteGroup(groupId) {
        try {
            // Get group info first to show in confirmation
            const groupResponse = await apiClient.getGroupById(groupId);
            const groupName = groupResponse?.success ? groupResponse.data.groupName : 'цю групу';
            
            const confirmMessage = `Ви впевнені, що хочете видалити групу "${groupName}"?\n\nУВАГА: Це дія незворотна! Всі студенти будуть відкріплені від групи.`;
            
            if (confirm(confirmMessage)) {
                const response = await apiClient.deleteGroup(groupId);
                if (response?.success || response?.status === 204) {
                    alert('Групу видалено успішно!');
                    this.loadGroupsData();
                    
                    // Close group students modal if it's open
                    const modal = document.getElementById('groupStudentsModal');
                    if (modal) {
                        this.closeGroupStudentsModal();
                    }
                } else {
                    throw new Error(response?.data?.message || response?.error || 'Невідома помилка');
                }
            }
        } catch (error) {
            console.error('Error deleting group:', error);
            alert('Помилка видалення групи: ' + error.message);
        }
    }

    // === UTILITY FUNCTIONS ===
    
    translateStudyForm(studyForm) {
        const translations = {
            'FULL_TIME': '🎓 Денна',
            'EVENING': '🌙 Вечірня',
            'PART_TIME': '📮 Заочна',
            'DISTANCE': '💻 Дистанційна'
        };
        return translations[studyForm] || studyForm;
    }
    
    translateEducationLevel(educationLevel) {
        const translations = {
            'BACHELOR': '🎓 Бакалавр',
            'MASTER': '🎯 Магістр',
            'PHD': '👨‍🔬 Аспірант'
        };
        return translations[educationLevel] || educationLevel;
    }
    
    translateAssessmentType(assessmentType) {
        const translations = {
            'EXAM': '📝 Екзамен',
            'TEST': '✅ Залік', 
            'DIFFERENTIATED_CREDIT': '📊 Диференційований залік',
            'COURSE_WORK': '📄 Курсова робота',
            'QUALIFICATION_WORK': '🎓 Кваліфікаційна робота',
            'ATTESTATION': '📋 Атестація',
            'STATE_EXAM': '🏛️ Державний іспит'
        };
        return translations[assessmentType] || assessmentType;
    }
    
    showLoadingSpinner(buttonElement, originalText) {
        if (buttonElement) {
            buttonElement.disabled = true;
            buttonElement.innerHTML = `<span class="loading-spinner"></span>${originalText}`;
        }
    }

    hideLoadingSpinner(buttonElement, originalText) {
        if (buttonElement) {
            buttonElement.disabled = false;
            buttonElement.innerHTML = originalText;
        }
    }

    // === ARCHIVE MANAGEMENT ===
    async loadArchiveData() {
        // Only ADMIN can access archive
        if (this.currentUser?.role !== 'ADMIN') {
            document.getElementById('archive-section').innerHTML = 
                '<div class="archive-empty"><div class="archive-empty-icon">🚫</div><h3>Доступ заборонено</h3><p>Тільки адміністратори мають доступ до архіву.</p></div>';
            return;
        }

        // Load archive statistics
        await this.loadArchiveStatistics();
        
        // Setup tabs and event listeners
        this.setupArchiveTabs();
        
        // Load archived groups by default
        await this.loadArchivedGroups();
    }

    async loadArchiveStatistics() {
        const statsDiv = document.getElementById('archive-stats');
        if (!statsDiv) return;

        statsDiv.innerHTML = '<div class="loading"></div>';

        try {
            const response = await apiClient.getArchiveStatistics();
            if (response?.success) {
                const stats = response.data;
                statsDiv.innerHTML = `
                    <div class="archive-stat-item">
                        <span class="archive-stat-label">🗂️ Архівні групи:</span>
                        <span class="archive-stat-value">${stats.totalArchivedGroups}</span>
                    </div>
                    <div class="archive-stat-item">
                        <span class="archive-stat-label">🎓 Архівні студенти:</span>
                        <span class="archive-stat-value">${stats.totalArchivedStudents}</span>
                    </div>
                    <div class="archive-stat-item">
                        <span class="archive-stat-label">📝 Архівні оцінки:</span>
                        <span class="archive-stat-value">${stats.totalArchivedGrades}</span>
                    </div>
                    <div class="archive-stat-item">
                        <span class="archive-stat-label">📅 Остання архівація:</span>
                        <span class="archive-stat-value">${stats.lastArchiveDate ? new Date(stats.lastArchiveDate).toLocaleDateString('uk-UA') : 'Немає даних'}</span>
                    </div>
                `;
            } else {
                statsDiv.innerHTML = '<p>Помилка завантаження статистики архіву</p>';
            }
        } catch (error) {
            console.error('Error loading archive statistics:', error);
            statsDiv.innerHTML = '<p>Помилка завантаження статистики</p>';
        }
    }

    setupArchiveTabs() {
        // Remove existing event listeners
        document.querySelectorAll('.archive-tab-btn').forEach(btn => {
            btn.replaceWith(btn.cloneNode(true));
        });

        // Add event listeners for archive tabs
        document.querySelectorAll('.archive-tab-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const tabName = e.target.dataset.archiveTab;
                
                // Remove active class from all tabs and contents
                document.querySelectorAll('.archive-tab-btn').forEach(b => b.classList.remove('active'));
                document.querySelectorAll('.archive-tab-content').forEach(c => c.classList.remove('active'));
                
                // Add active class to clicked tab
                e.target.classList.add('active');
                document.getElementById(`archive-${tabName}-tab`).classList.add('active');
                
                // Load data for the selected tab
                switch (tabName) {
                    case 'groups':
                        await this.loadArchivedGroups();
                        break;
                    case 'students':
                        await this.loadArchivedStudents();
                        break;
                    case 'grades':
                        await this.loadArchivedGrades();
                        break;
                }
            });
        });

        // Setup search functionality
        document.getElementById('search-archive-groups')?.addEventListener('click', () => {
            this.searchArchivedGroups();
        });

        document.getElementById('search-archive-students')?.addEventListener('click', () => {
            this.searchArchivedStudents();
        });

        // Setup enter key search
        document.getElementById('archive-groups-search')?.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.searchArchivedGroups();
        });

        document.getElementById('archive-students-search')?.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.searchArchivedStudents();
        });
    }

    async loadArchivedGroups() {
        const tbody = document.getElementById('archive-groups-tbody');
        if (!tbody) return;

        tbody.innerHTML = '<tr><td colspan="8"><div class="loading"></div></td></tr>';

        try {
            const response = await apiClient.getArchivedGroups();
            if (response?.success && Array.isArray(response.data)) {
                this.renderArchivedGroupsTable(response.data);
            } else {
                tbody.innerHTML = '<tr><td colspan="8">Помилка завантаження архівних груп</td></tr>';
            }
        } catch (error) {
            console.error('Error loading archived groups:', error);
            tbody.innerHTML = '<tr><td colspan="8">Помилка завантаження даних</td></tr>';
        }
    }

    async loadArchivedStudents() {
        const tbody = document.getElementById('archive-students-tbody');
        if (!tbody) return;

        tbody.innerHTML = '<tr><td colspan="8"><div class="loading"></div></td></tr>';

        try {
            const response = await apiClient.getArchivedStudents();
            if (response?.success && Array.isArray(response.data)) {
                this.renderArchivedStudentsTable(response.data);
            } else {
                tbody.innerHTML = '<tr><td colspan="8">Помилка завантаження архівних студентів</td></tr>';
            }
        } catch (error) {
            console.error('Error loading archived students:', error);
            tbody.innerHTML = '<tr><td colspan="8">Помилка завантаження даних</td></tr>';
        }
    }

    async loadArchivedGrades() {
        const tbody = document.getElementById('archive-grades-tbody');
        if (!tbody) return;

        tbody.innerHTML = '<tr><td colspan="8"><div class="loading"></div></td></tr>';

        try {
            const response = await apiClient.getArchivedGrades();
            if (response?.success && Array.isArray(response.data)) {
                this.renderArchivedGradesTable(response.data);
            } else {
                tbody.innerHTML = '<tr><td colspan="8">Помилка завантаження архівних оцінок</td></tr>';
            }
        } catch (error) {
            console.error('Error loading archived grades:', error);
            tbody.innerHTML = '<tr><td colspan="8">Помилка завантаження даних</td></tr>';
        }
    }

    renderArchivedGroupsTable(groups) {
        const tbody = document.getElementById('archive-groups-tbody');
        if (!tbody) return;

        if (!groups.length) {
            tbody.innerHTML = '<tr><td colspan="8"><div class="archive-empty"><div class="archive-empty-icon">📭</div><p>Архівних груп не знайдено</p></div></td></tr>';
            return;
        }

        tbody.innerHTML = groups.map(group => `
            <tr>
                <td>${group.groupName || 'N/A'}</td>
                <td>${group.groupCode || 'N/A'}</td>
                <td>${group.courseYear || 'N/A'}</td>
                <td>${this.translateStudyForm(group.studyForm) || 'N/A'}</td>
                <td class="archive-date">${group.archivedAt ? new Date(group.archivedAt).toLocaleString('uk-UA') : 'N/A'}</td>
                <td>${group.archivedBy || 'N/A'}</td>
                <td class="archive-reason" title="${group.archiveReason || ''}">${group.archiveReason || 'N/A'}</td>
                <td>
                    <div class="archive-actions">
                        <button class="btn btn-sm btn-info" onclick="dashboard.viewArchivedGroupStudents(${group.originalGroupId})">👥 Студенти</button>
                        <button class="btn btn-sm btn-danger-outline" onclick="dashboard.deleteArchivedGroup(${group.id})">🗑️ Видалити</button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    renderArchivedStudentsTable(students) {
        const tbody = document.getElementById('archive-students-tbody');
        if (!tbody) return;

        if (!students.length) {
            tbody.innerHTML = '<tr><td colspan="8"><div class="archive-empty"><div class="archive-empty-icon">📭</div><p>Архівних студентів не знайдено</p></div></td></tr>';
            return;
        }

        tbody.innerHTML = students.map(student => `
            <tr>
                <td>${student.studentNumber || 'N/A'}</td>
                <td>${student.groupName || 'Не призначено'}</td>
                <td>${student.enrollmentYear || 'N/A'}</td>
                <td>${this.translateStudyForm(student.studyForm) || 'N/A'}</td>
                <td class="archive-date">${student.archivedAt ? new Date(student.archivedAt).toLocaleString('uk-UA') : 'N/A'}</td>
                <td>${student.archivedBy || 'N/A'}</td>
                <td class="archive-reason" title="${student.archiveReason || ''}">${student.archiveReason || 'N/A'}</td>
                <td>
                    <div class="archive-actions">
                        <button class="btn btn-sm btn-info" onclick="dashboard.viewArchivedStudentGrades(${student.originalStudentId})">📝 Оцінки</button>
                        <button class="btn btn-sm btn-danger-outline" onclick="dashboard.deleteArchivedStudent(${student.id})">🗑️ Видалити</button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    renderArchivedGradesTable(grades) {
        const tbody = document.getElementById('archive-grades-tbody');
        if (!tbody) return;

        if (!grades.length) {
            tbody.innerHTML = '<tr><td colspan="8"><div class="archive-empty"><div class="archive-empty-icon">📭</div><p>Архівних оцінок не знайдено</p></div></td></tr>';
            return;
        }

        tbody.innerHTML = grades.map(grade => `
            <tr>
                <td>${grade.studentNumber || 'N/A'}</td>
                <td>${grade.subjectName || 'N/A'}</td>
                <td>${this.translateGradeType(grade.gradeType) || 'N/A'}</td>
                <td><strong>${grade.gradeValue || 'N/A'}</strong></td>
                <td class="archive-date">${grade.originalGradeDate ? new Date(grade.originalGradeDate).toLocaleDateString('uk-UA') : 'N/A'}</td>
                <td class="archive-date">${grade.archivedAt ? new Date(grade.archivedAt).toLocaleString('uk-UA') : 'N/A'}</td>
                <td>${grade.archivedBy || 'N/A'}</td>
                <td>
                    <div class="archive-actions">
                        <button class="btn btn-sm btn-danger-outline" onclick="dashboard.deleteArchivedGrade(${grade.id})">🗑️ Видалити</button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    async searchArchivedGroups() {
        const query = document.getElementById('archive-groups-search')?.value?.trim();
        if (!query) {
            await this.loadArchivedGroups();
            return;
        }

        try {
            const response = await apiClient.searchArchivedGroups(query);
            if (response?.success && Array.isArray(response.data)) {
                this.renderArchivedGroupsTable(response.data);
            }
        } catch (error) {
            console.error('Error searching archived groups:', error);
        }
    }

    async searchArchivedStudents() {
        const query = document.getElementById('archive-students-search')?.value?.trim();
        if (!query) {
            await this.loadArchivedStudents();
            return;
        }

        try {
            const response = await apiClient.searchArchivedStudents(query);
            if (response?.success && Array.isArray(response.data)) {
                this.renderArchivedStudentsTable(response.data);
            }
        } catch (error) {
            console.error('Error searching archived students:', error);
        }
    }

    async viewArchivedGroupStudents(originalGroupId) {
        try {
            const response = await apiClient.getArchivedStudentsByGroup(originalGroupId);
            if (response?.success) {
                // Switch to students tab and filter by group
                document.querySelector('[data-archive-tab="students"]').click();
                this.renderArchivedStudentsTable(response.data);
            }
        } catch (error) {
            console.error('Error loading archived students by group:', error);
            alert('Помилка завантаження студентів групи');
        }
    }

    async viewArchivedStudentGrades(originalStudentId) {
        try {
            const response = await apiClient.getArchivedGradesByStudent(originalStudentId);
            if (response?.success) {
                // Switch to grades tab and filter by student
                document.querySelector('[data-archive-tab="grades"]').click();
                this.renderArchivedGradesTable(response.data);
            }
        } catch (error) {
            console.error('Error loading archived grades by student:', error);
            alert('Помилка завантаження оцінок студента');
        }
    }

    async deleteArchivedGroup(archivedGroupId) {
        if (confirm('Ви впевнені, що хочете НАЗАВЖДИ видалити цю архівну групу? Цю дію неможливо скасувати!')) {
            try {
                const response = await apiClient.deleteArchivedGroup(archivedGroupId);
                if (response?.success) {
                    alert('Архівну групу видалено назавжди');
                    await this.loadArchivedGroups();
                    await this.loadArchiveStatistics(); // Update statistics
                } else {
                    alert('Помилка видалення архівної групи: ' + (response?.data || 'Невідома помилка'));
                }
            } catch (error) {
                console.error('Error deleting archived group:', error);
                alert('Помилка: ' + error.message);
            }
        }
    }

    async deleteArchivedStudent(archivedStudentId) {
        if (confirm('Ви впевнені, що хочете НАЗАВЖДИ видалити цього архівного студента? Цю дію неможливо скасувати!')) {
            try {
                const response = await apiClient.deleteArchivedStudent(archivedStudentId);
                if (response?.success) {
                    alert('Архівного студента видалено назавжди');
                    await this.loadArchivedStudents();
                    await this.loadArchiveStatistics(); // Update statistics
                } else {
                    alert('Помилка видалення архівного студента: ' + (response?.data || 'Невідома помилка'));
                }
            } catch (error) {
                console.error('Error deleting archived student:', error);
                alert('Помилка: ' + error.message);
            }
        }
    }

    async deleteArchivedGrade(archivedGradeId) {
        if (confirm('Ви впевнені, що хочете НАЗАВЖДИ видалити цю архівну оцінку? Цю дію неможливо скасувати!')) {
            try {
                const response = await apiClient.deleteArchivedGrade(archivedGradeId);
                if (response?.success) {
                    alert('Архівну оцінку видалено назавжди');
                    await this.loadArchivedGrades();
                    await this.loadArchiveStatistics(); // Update statistics
                } else {
                    alert('Помилка видалення архівної оцінки: ' + (response?.data || 'Невідома помилка'));
                }
            } catch (error) {
                console.error('Error deleting archived grade:', error);
                alert('Помилка: ' + error.message);
            }
        }
    }

    // === ROLE-BASED UI CONFIGURATION ===
    configureActionButtons() {
        // Configure action buttons based on user role
        const role = this.currentUser?.role;
        
        const addButtons = {
            'add-group': role === 'MANAGER', // Only managers can create groups
            'add-subject': this.rolePermissions?.canCreate,
            'add-user-btn': this.rolePermissions?.canCreate,
            'add-grade-btn': role === 'TEACHER' // Only teachers can add grades
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
