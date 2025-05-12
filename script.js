// Handle user login
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
  
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
  
    try {
      const response = await fetch('http://localhost:8080/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });
  
      if (!response.ok) {
        alert('Login failed!');
        return;
      }
  
      const { token } = await response.json();
      localStorage.setItem('jwt_token', token);
      window.location.href = 'dashboard.html';  // Redirect to dashboard
    } catch (error) {
      console.error('Error:', error);
    }
  });
  
  // Handle user signup
  document.getElementById('signupForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
  
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const role = document.getElementById('role').value;
  
    try {
      const response = await fetch('http://localhost:8080/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, role })
      });
  
      if (!response.ok) {
        alert('Signup failed!');
        return;
      }
  
      const { token } = await response.json();
      localStorage.setItem('jwt_token', token);
      window.location.href = 'dashboard.html';  // Redirect to dashboard
    } catch (error) {
      console.error('Error:', error);
    }
  });
  
  // Fetch tasks and display in the dashboard
  async function fetchTasks() {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      alert("Please login first!");
      return;
    }
  
    try {
      const response = await fetch('http://localhost:8080/tasks', {
        method: 'GET',
        headers: {
          'Authorization': token,
          'Content-Type': 'application/json'
        }
      });
  
      if (!response.ok) {
        throw new Error('Error fetching tasks');
      }
  
      const tasks = await response.json();
      displayTasks(tasks);
    } catch (error) {
      console.error('Error:', error);
    }
  }
  
  // Display tasks on the dashboard
  function displayTasks(tasks) {
    const tasksContainer = document.getElementById('tasksContainer');
    tasksContainer.innerHTML = '';
  
    tasks.forEach(task => {
      const taskElement = document.createElement('div');
      taskElement.classList.add('task');
      taskElement.innerHTML = `
        <h3>${task.title}</h3>
        <p>${task.description}</p>
        <p>Status: ${task.status}</p>
        <p>Assignee: ${task.assignee}</p>
      `;
      tasksContainer.appendChild(taskElement);
    });
  }
  
  // Handle logout
  document.getElementById('logoutBtn')?.addEventListener('click', () => {
    localStorage.removeItem('jwt_token');
    window.location.href = 'index.html';  // Redirect to login page
  });
  
  // Handle task creation
  document.getElementById('createTaskForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
  
    const title = document.getElementById('title').value;
    const description = document.getElementById('description').value;
    const assignee = document.getElementById('assignee').value;
  
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      alert("Please login first!");
      return;
    }
  
    try {
      const response = await fetch('http://localhost:8080/tasks', {
        method: 'POST',
        headers: {
          'Authorization': token,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ title, description, assignee })
      });
  
      if (!response.ok) {
        alert('Failed to create task');
        return;
      }
  
      alert('Task created successfully');
      window.location.href = 'dashboard.html';  // Redirect to dashboard
    } catch (error) {
      console.error('Error:', error);
    }
  });
  
  // Call fetchTasks when the dashboard page is loaded
  if (window.location.pathname === '/dashboard.html') {
    fetchTasks();
  }
  