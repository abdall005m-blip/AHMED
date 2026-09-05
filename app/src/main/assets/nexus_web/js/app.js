// Nexus Web App — Main Application Module
// Orchestrates all screens: Home, Notifications, AI Hub, Tasks, Schedule, Search, Settings, Entertainment

import { realtimeStore } from './store/realtimeStore.js';
import { HeartCounter } from './components/heartCounter.js';
import { GamesManager } from './components/gamesManager.js';
import { AZKAR_DATA } from './data/azkarData.js';
import { SURAH_LIST, fetchFullSurahText } from './data/quranData.js';
import { PrayerTimesCalculator } from './utils/prayerTimes.js';

class NexusApp {
  constructor() {
    this.currentUser = null; // 'ahmed' or 'rody' after chat login
    this.currentScreen = 'home';
    this.gamesManager = null;
    this.heartCounter = null;
    this.theme = localStorage.getItem('nexus_theme') || 'light';
    this.lang = localStorage.getItem('nexus_lang') || (navigator.language.startsWith('ar') ? 'ar' : 'en');
    this.init();
  }

  init() {
    this.applyTheme();
    this.applyLang();
    this.renderApp();
    this.bindNavigation();

    // Listen for real-time updates
    realtimeStore.subscribe('*', () => {
      // Re-render current screen on any data change
      this.renderCurrentScreen();
    });
  }

  applyTheme() {
    document.documentElement.setAttribute('data-theme', this.theme);
  }

  applyLang() {
    document.body.setAttribute('dir', this.lang === 'ar' ? 'rtl' : 'ltr');
  }

  toggleTheme() {
    this.theme = this.theme === 'dark' ? 'light' : 'dark';
    localStorage.setItem('nexus_theme', this.theme);
    this.applyTheme();
  }

  toggleLang() {
    this.lang = this.lang === 'ar' ? 'en' : 'ar';
    localStorage.setItem('nexus_lang', this.lang);
    this.applyLang();
    this.renderApp();
  }

  t(ar, en) {
    return this.lang === 'ar' ? ar : en;
  }

  navigate(screen) {
    this.currentScreen = screen;
    this.renderCurrentScreen();
    // Update active nav
    document.querySelectorAll('.nav-item').forEach(el => {
      el.classList.toggle('active', el.dataset.screen === screen);
    });
  }

  // =========================================================
  // MAIN APP SHELL
  // =========================================================
  renderApp() {
    const app = document.getElementById('app');
    app.innerHTML = `
      <!-- Top Bar -->
      <header class="glass-panel" style="
        position: sticky; top: 0; z-index: 100;
        padding: 12px 20px;
        border-radius: 0 0 20px 20px;
        display: flex; align-items: center; justify-content: space-between;
      ">
        <div style="display: flex; align-items: center; gap: 10px;">
          <span style="font-size: 24px;">✨</span>
          <span style="font-size: 20px; font-weight: 800; background: linear-gradient(135deg, var(--primary), var(--accent-pink)); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">Nexus</span>
        </div>
        <div style="display: flex; align-items: center; gap: 8px;">
          <button id="lang-toggle" class="glass-btn-secondary" style="padding: 6px 10px; border-radius: 10px; font-size: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border);">
            ${this.lang === 'ar' ? 'EN' : 'عربي'}
          </button>
          <button id="theme-toggle" class="glass-btn-secondary" style="padding: 6px 10px; border-radius: 10px; font-size: 16px; cursor: pointer; border: 1px solid var(--surface-glass-border);">
            ${this.theme === 'dark' ? '☀️' : '🌙'}
          </button>
        </div>
      </header>

      <!-- Main Content Area -->
      <main id="screen-content" style="flex: 1; padding: 16px 16px 90px 16px; max-width: 600px; margin: 0 auto; width: 100%;"></main>

      <!-- Bottom Navigation -->
      <nav class="glass-panel" style="
        position: fixed; bottom: 0; left: 0; right: 0; z-index: 100;
        padding: 8px 8px 12px 8px;
        border-radius: 20px 20px 0 0;
        display: flex; justify-content: space-around; align-items: center;
      ">
        <button class="nav-item active" data-screen="home" title="${this.t('الرئيسية','Home')}">
          <span style="font-size: 22px;">🏠</span>
          <span class="nav-label">${this.t('الرئيسية','Home')}</span>
        </button>
        <button class="nav-item" data-screen="notifications" title="${this.t('الإشعارات','Notifications')}">
          <span style="font-size: 22px;">🔔</span>
          <span class="nav-label">${this.t('إشعارات','Alerts')}</span>
        </button>
        <button class="nav-item" data-screen="tasks" title="${this.t('المهام','Tasks')}">
          <span style="font-size: 22px;">✅</span>
          <span class="nav-label">${this.t('المهام','Tasks')}</span>
        </button>
        <button class="nav-item" data-screen="entertainment" title="${this.t('ترفيه','Fun')}">
          <span style="font-size: 22px;">🎮</span>
          <span class="nav-label">${this.t('ترفيه','Fun')}</span>
        </button>
        <button class="nav-item" data-screen="settings" title="${this.t('الإعدادات','Settings')}">
          <span style="font-size: 22px;">⚙️</span>
          <span class="nav-label">${this.t('المزيد','More')}</span>
        </button>
      </nav>
    `;

    this.bindNavigation();
    this.renderCurrentScreen();
  }

  bindNavigation() {
    document.querySelectorAll('.nav-item').forEach(btn => {
      btn.onclick = () => this.navigate(btn.dataset.screen);
    });
    const themeBtn = document.getElementById('theme-toggle');
    if (themeBtn) themeBtn.onclick = () => { this.toggleTheme(); this.renderApp(); };
    const langBtn = document.getElementById('lang-toggle');
    if (langBtn) langBtn.onclick = () => this.toggleLang();
  }

  renderCurrentScreen() {
    const content = document.getElementById('screen-content');
    if (!content) return;
    switch (this.currentScreen) {
      case 'home': this.renderHome(content); break;
      case 'notifications': this.renderNotifications(content); break;
      case 'tasks': this.renderTasks(content); break;
      case 'aihub': this.renderAIHub(content); break;
      case 'schedule': this.renderSchedule(content); break;
      case 'search': this.renderSearch(content); break;
      case 'settings': this.renderSettings(content); break;
      case 'entertainment': this.renderEntertainment(content); break;
      case 'chat': this.renderChat(content); break;
      case 'chat-login': this.renderChatLogin(content); break;
      case 'games': this.renderGames(content); break;
      case 'islamic': this.renderIslamic(content); break;
      case 'mood': this.renderMood(content); break;
      case 'quran': this.renderQuran(content); break;
      case 'azkar': this.renderAzkar(content); break;
      case 'bubble-styles': this.renderBubbleStyles(content); break;
      default: this.renderHome(content);
    }
  }

  // =========================================================
  // HOME SCREEN
  // =========================================================
  renderHome(el) {
    const s = realtimeStore.state;
    const todayTasks = s.tasks.filter(t => t.category === 'today');
    const completedCount = todayTasks.filter(t => t.completed).length;
    const prayerTimes = PrayerTimesCalculator.getTimes();

    el.innerHTML = `
      <div style="margin-bottom: 20px;">
        <h1 style="font-size: 26px; font-weight: 800; margin-bottom: 4px;">
          ${this.t('مرحباً بك في Nexus','Welcome to Nexus')} 👋
        </h1>
        <p style="color: var(--text-muted); font-size: 14px;">
          ${this.t('لوحة تحكمك الشخصية الذكية','Your smart personal dashboard')}
        </p>
      </div>

      <!-- Quick Stats Row -->
      <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; margin-bottom: 18px;">
        <div class="glass-card" style="padding: 14px; text-align: center;">
          <div style="font-size: 24px; font-weight: 800; color: var(--primary);">${s.notifications.length}</div>
          <div style="font-size: 11px; color: var(--text-muted);">${this.t('إشعار','Alerts')}</div>
        </div>
        <div class="glass-card" style="padding: 14px; text-align: center;">
          <div style="font-size: 24px; font-weight: 800; color: var(--accent-emerald);">${completedCount}/${todayTasks.length}</div>
          <div style="font-size: 11px; color: var(--text-muted);">${this.t('مهام اليوم','Today')}</div>
        </div>
        <div class="glass-card" style="padding: 14px; text-align: center;">
          <div style="font-size: 24px; font-weight: 800; color: var(--accent-amber);">${s.aiApps.filter(a => a.pinned).length}</div>
          <div style="font-size: 11px; color: var(--text-muted);">AI</div>
        </div>
      </div>

      <!-- Prayer Times Quick Card -->
      <div class="glass-card" style="padding: 16px; margin-bottom: 16px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
          <span style="font-weight: 700; font-size: 15px;">🕌 ${this.t('مواقيت الصلاة','Prayer Times')}</span>
        </div>
        <div style="display: grid; grid-template-columns: repeat(5, 1fr); gap: 6px; text-align: center;">
          ${[
            { name: this.t('الفجر','Fajr'), time: prayerTimes.fajr },
            { name: this.t('الظهر','Dhuhr'), time: prayerTimes.dhuhr },
            { name: this.t('العصر','Asr'), time: prayerTimes.asr },
            { name: this.t('المغرب','Maghrib'), time: prayerTimes.maghrib },
            { name: this.t('العشاء','Isha'), time: prayerTimes.isha }
          ].map(p => `
            <div>
              <div style="font-size: 11px; color: var(--text-muted); margin-bottom: 2px;">${p.name}</div>
              <div style="font-size: 13px; font-weight: 700;">${p.time}</div>
            </div>
          `).join('')}
        </div>
      </div>

      <!-- Quick Access Cards -->
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 16px;">
        <div class="glass-card" style="padding: 16px; cursor: pointer;" onclick="nexusApp.navigate('aihub')">
          <div style="font-size: 28px; margin-bottom: 6px;">🤖</div>
          <div style="font-weight: 700; font-size: 14px;">${this.t('مركز AI','AI Hub')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">${s.aiApps.length} ${this.t('تطبيق','apps')}</div>
        </div>
        <div class="glass-card" style="padding: 16px; cursor: pointer;" onclick="nexusApp.navigate('schedule')">
          <div style="font-size: 28px; margin-bottom: 6px;">📅</div>
          <div style="font-weight: 700; font-size: 14px;">${this.t('الجدول','Schedule')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">${s.schedules.length} ${this.t('موعد','events')}</div>
        </div>
        <div class="glass-card" style="padding: 16px; cursor: pointer;" onclick="nexusApp.navigate('entertainment')">
          <div style="font-size: 28px; margin-bottom: 6px;">🎮</div>
          <div style="font-weight: 700; font-size: 14px;">${this.t('ترفيه','Entertainment')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">${this.t('ألعاب ودردشة','Games & Chat')}</div>
        </div>
        <div class="glass-card" style="padding: 16px; cursor: pointer;" onclick="nexusApp.navigate('search')">
          <div style="font-size: 28px; margin-bottom: 6px;">🔍</div>
          <div style="font-weight: 700; font-size: 14px;">${this.t('البحث','Search')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">${this.t('بحث شامل','Unified')}</div>
        </div>
      </div>

      <!-- Pinned AI Apps -->
      <div style="margin-bottom: 16px;">
        <h3 style="font-size: 15px; font-weight: 700; margin-bottom: 10px;">${this.t('تطبيقات AI المثبتة','Pinned AI')}</h3>
        <div style="display: flex; gap: 10px; overflow-x: auto; padding-bottom: 6px;">
          ${s.aiApps.filter(a => a.pinned).map(app => `
            <a href="${app.url}" target="_blank" rel="noopener" class="glass-card" style="
              padding: 12px 16px; min-width: 100px; text-align: center; text-decoration: none; color: inherit; flex-shrink: 0;
            ">
              <div style="font-size: 26px; margin-bottom: 4px;">${app.emoji}</div>
              <div style="font-size: 12px; font-weight: 700;">${app.name}</div>
            </a>
          `).join('')}
        </div>
      </div>
    `;
  }

  // =========================================================
  // NOTIFICATIONS SCREEN
  // =========================================================
  renderNotifications(el) {
    const notifs = realtimeStore.state.notifications;
    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">🔔 ${this.t('الإشعارات','Notifications')}</h2>
      ${notifs.length === 0 ? `
        <div class="glass-card" style="padding: 40px; text-align: center;">
          <div style="font-size: 48px; margin-bottom: 12px;">💬</div>
          <div style="font-size: 15px; color: var(--text-muted);">${this.t('لسه مفيش إشعارات هنا','No notifications yet')}</div>
        </div>
      ` : notifs.map(n => `
        <div class="glass-card" style="padding: 14px; margin-bottom: 10px; display: flex; gap: 12px; align-items: flex-start;">
          <div style="
            width: 40px; height: 40px; border-radius: 12px;
            background: ${n.priority === 'HIGH' ? 'rgba(244,63,94,0.15)' : n.priority === 'MEDIUM' ? 'rgba(245,158,11,0.15)' : 'rgba(100,116,139,0.1)'};
            display: flex; align-items: center; justify-content: center; flex-shrink: 0; font-size: 18px;
          ">${n.priority === 'HIGH' ? '🔴' : n.priority === 'MEDIUM' ? '🟡' : '⚪'}</div>
          <div style="flex: 1; min-width: 0;">
            <div style="font-weight: 700; font-size: 13px; margin-bottom: 2px;">${n.appName}</div>
            <div style="font-weight: 600; font-size: 14px; margin-bottom: 2px;">${n.title}</div>
            <div style="font-size: 13px; color: var(--text-muted); overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">${n.content}</div>
          </div>
          <div style="font-size: 11px; color: var(--text-muted); flex-shrink: 0;">${n.time}</div>
        </div>
      `).join('')}
    `;
  }

  // =========================================================
  // AI HUB SCREEN
  // =========================================================
  renderAIHub(el) {
    const apps = realtimeStore.state.aiApps;
    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">🤖 ${this.t('مركز الذكاء الاصطناعي','AI Hub')}</h2>
      <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px;">
        ${apps.map(app => `
          <a href="${app.url}" target="_blank" rel="noopener" class="glass-card" style="padding: 18px; text-decoration: none; color: inherit; text-align: center;">
            <div style="font-size: 36px; margin-bottom: 8px;">${app.emoji}</div>
            <div style="font-weight: 700; font-size: 15px; margin-bottom: 4px;">${app.name}</div>
            <div style="font-size: 12px; color: var(--text-muted); line-height: 1.4;">${app.desc}</div>
            ${app.pinned ? '<div style="margin-top: 6px; font-size: 11px; color: var(--accent-amber);">📌 مثبت</div>' : ''}
          </a>
        `).join('')}
      </div>
    `;
  }

  // =========================================================
  // TASKS SCREEN
  // =========================================================
  renderTasks(el) {
    const tasks = realtimeStore.state.tasks;
    const categories = [
      { key: 'today', label: this.t('اليوم','Today'), icon: '📋' },
      { key: 'upcoming', label: this.t('القادمة','Upcoming'), icon: '📅' },
      { key: 'done', label: this.t('المنجزة','Done'), icon: '✅' }
    ];

    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">✅ ${this.t('المهام','Tasks')}</h2>

      <!-- Add Task -->
      <div class="glass-card" style="padding: 14px; margin-bottom: 16px; display: flex; gap: 10px;">
        <input id="new-task-input" type="text" placeholder="${this.t('أضف مهمة جديدة...','Add new task...')}" style="
          flex: 1; padding: 10px 14px; border-radius: 12px; border: 1px solid var(--surface-glass-border);
          background: var(--surface-glass); color: var(--text-main); font-size: 14px; outline: none;
        ">
        <button id="add-task-btn" class="glass-btn" style="padding: 10px 16px;">➕</button>
      </div>

      ${categories.map(cat => {
        const catTasks = tasks.filter(t => cat.key === 'done' ? t.completed : (!t.completed && t.category === cat.key));
        return `
          <div style="margin-bottom: 18px;">
            <h3 style="font-size: 15px; font-weight: 700; margin-bottom: 8px;">${cat.icon} ${cat.label} (${catTasks.length})</h3>
            ${catTasks.length === 0 ? `<div style="text-align: center; padding: 16px; font-size: 13px; color: var(--text-muted);">${this.t('لا مهام هنا حالياً','No tasks here')}</div>` :
            catTasks.map(task => `
              <div class="glass-card" style="padding: 12px 14px; margin-bottom: 8px; display: flex; align-items: center; gap: 10px;">
                <button class="task-check-btn" data-id="${task.id}" style="
                  width: 24px; height: 24px; border-radius: 8px; border: 2px solid ${task.completed ? 'var(--accent-emerald)' : 'var(--text-muted)'};
                  background: ${task.completed ? 'var(--accent-emerald)' : 'transparent'}; cursor: pointer; display: flex; align-items: center; justify-content: center;
                  color: white; font-size: 14px; flex-shrink: 0;
                ">${task.completed ? '✓' : ''}</button>
                <div style="flex: 1;">
                  <div style="font-weight: 600; font-size: 14px; text-decoration: ${task.completed ? 'line-through' : 'none'}; opacity: ${task.completed ? 0.6 : 1};">${task.title}</div>
                  ${task.desc ? `<div style="font-size: 12px; color: var(--text-muted);">${task.desc}</div>` : ''}
                </div>
                <span style="
                  font-size: 10px; padding: 3px 8px; border-radius: 8px; font-weight: 700;
                  background: ${task.priority === 'HIGH' ? 'rgba(244,63,94,0.15)' : task.priority === 'MEDIUM' ? 'rgba(245,158,11,0.15)' : 'rgba(100,116,139,0.1)'};
                  color: ${task.priority === 'HIGH' ? '#e11d48' : task.priority === 'MEDIUM' ? '#d97706' : '#64748b'};
                ">${task.priority}</span>
              </div>
            `).join('')}
          </div>
        `;
      }).join('')}
    `;

    // Bind task actions
    document.getElementById('add-task-btn')?.addEventListener('click', () => {
      const input = document.getElementById('new-task-input');
      if (input && input.value.trim()) {
        realtimeStore.state.tasks.push({
          id: Date.now(),
          title: input.value.trim(),
          desc: '',
          category: 'today',
          priority: 'MEDIUM',
          completed: false
        });
        realtimeStore.emit('task_added', {});
        input.value = '';
      }
    });

    document.querySelectorAll('.task-check-btn').forEach(btn => {
      btn.onclick = () => {
        const id = parseInt(btn.dataset.id, 10);
        const task = realtimeStore.state.tasks.find(t => t.id === id);
        if (task) {
          task.completed = !task.completed;
          if (task.completed) task.category = 'done';
          realtimeStore.emit('task_updated', { id });
        }
      };
    });
  }

  // =========================================================
  // SCHEDULE SCREEN
  // =========================================================
  renderSchedule(el) {
    const schedules = realtimeStore.state.schedules;
    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">📅 ${this.t('الجدول والمواعيد','Schedule')}</h2>
      ${schedules.length === 0 ? `
        <div class="glass-card" style="padding: 40px; text-align: center;">
          <div style="font-size: 48px; margin-bottom: 12px;">📅</div>
          <div style="font-size: 15px; color: var(--text-muted);">${this.t('لا مواعيد حالياً','No events yet')}</div>
        </div>
      ` : schedules.map(s => `
        <div class="glass-card" style="padding: 14px; margin-bottom: 10px; display: flex; gap: 12px; align-items: center;">
          <div style="
            width: 50px; height: 50px; border-radius: 14px;
            background: linear-gradient(135deg, var(--primary-light), var(--primary));
            display: flex; align-items: center; justify-content: center;
            color: white; font-weight: 800; font-size: 14px; flex-shrink: 0;
          ">${s.time}</div>
          <div style="flex: 1;">
            <div style="font-weight: 700; font-size: 15px;">${s.title}</div>
            <div style="font-size: 12px; color: var(--text-muted);">${s.category} — ${s.note}</div>
          </div>
        </div>
      `).join('')}
    `;
  }

  // =========================================================
  // SEARCH SCREEN
  // =========================================================
  renderSearch(el) {
    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">🔍 ${this.t('البحث الشامل','Unified Search')}</h2>
      <div class="glass-card" style="padding: 14px; margin-bottom: 16px;">
        <input id="search-input" type="text" placeholder="${this.t('ابحث في كل شيء...','Search everything...')}" style="
          width: 100%; padding: 12px 16px; border-radius: 14px; border: 1px solid var(--surface-glass-border);
          background: var(--surface-glass); color: var(--text-main); font-size: 15px; outline: none;
        ">
      </div>
      <div id="search-results"></div>
    `;

    document.getElementById('search-input')?.addEventListener('input', (e) => {
      const q = e.target.value.trim().toLowerCase();
      const results = document.getElementById('search-results');
      if (!q) { results.innerHTML = ''; return; }

      const s = realtimeStore.state;
      let html = '';

      // Search notifications
      const nots = s.notifications.filter(n => n.title.toLowerCase().includes(q) || n.content.toLowerCase().includes(q));
      if (nots.length) {
        html += `<h4 style="margin: 10px 0 6px; font-size: 13px; color: var(--text-muted);">🔔 ${this.t('إشعارات','Notifications')}</h4>`;
        nots.forEach(n => { html += `<div class="glass-card" style="padding: 10px; margin-bottom: 6px; font-size: 13px;"><b>${n.appName}:</b> ${n.title}</div>`; });
      }
      // Search tasks
      const tsks = s.tasks.filter(t => t.title.toLowerCase().includes(q));
      if (tsks.length) {
        html += `<h4 style="margin: 10px 0 6px; font-size: 13px; color: var(--text-muted);">✅ ${this.t('مهام','Tasks')}</h4>`;
        tsks.forEach(t => { html += `<div class="glass-card" style="padding: 10px; margin-bottom: 6px; font-size: 13px;">${t.completed ? '☑️' : '⬜'} ${t.title}</div>`; });
      }
      // Search AI apps
      const ais = s.aiApps.filter(a => a.name.toLowerCase().includes(q));
      if (ais.length) {
        html += `<h4 style="margin: 10px 0 6px; font-size: 13px; color: var(--text-muted);">🤖 AI</h4>`;
        ais.forEach(a => { html += `<div class="glass-card" style="padding: 10px; margin-bottom: 6px; font-size: 13px;">${a.emoji} ${a.name}</div>`; });
      }

      if (!html) html = `<div style="text-align: center; padding: 20px; color: var(--text-muted);">${this.t('لا نتائج','No results')}</div>`;
      results.innerHTML = html;
    });
  }

  // =========================================================
  // SETTINGS SCREEN
  // =========================================================
  renderSettings(el) {
    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">⚙️ ${this.t('الإعدادات','Settings')}</h2>

      <div class="glass-card" style="padding: 16px; margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center; cursor: pointer;" onclick="nexusApp.toggleTheme(); nexusApp.renderApp();">
        <span style="font-weight: 600;">${this.theme === 'dark' ? '☀️' : '🌙'} ${this.t('الوضع الليلي','Dark Mode')}</span>
        <span style="font-size: 13px; color: var(--primary); font-weight: 700;">${this.theme === 'dark' ? this.t('مفعل','ON') : this.t('معطل','OFF')}</span>
      </div>

      <div class="glass-card" style="padding: 16px; margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center; cursor: pointer;" onclick="nexusApp.toggleLang();">
        <span style="font-weight: 600;">🌐 ${this.t('اللغة','Language')}</span>
        <span style="font-size: 13px; color: var(--primary); font-weight: 700;">${this.lang === 'ar' ? 'العربية' : 'English'}</span>
      </div>

      <div class="glass-card" style="padding: 16px; margin-bottom: 12px; cursor: pointer;" onclick="nexusApp.navigate('aihub')">
        <span style="font-weight: 600;">🤖 ${this.t('مركز الذكاء الاصطناعي','AI Hub')}</span>
      </div>

      <div class="glass-card" style="padding: 16px; margin-bottom: 12px; cursor: pointer;" onclick="nexusApp.navigate('schedule')">
        <span style="font-weight: 600;">📅 ${this.t('الجدول والمواعيد','Schedule')}</span>
      </div>

      <div class="glass-card" style="padding: 16px; margin-bottom: 12px;">
        <span style="font-weight: 600;">📱 ${this.t('إصدار التطبيق','App Version')}</span>
        <span style="float: left; font-size: 13px; color: var(--text-muted);">Nexus Web v1.0.0</span>
      </div>
    `;
  }

  // =========================================================
  // ENTERTAINMENT HUB
  // =========================================================
  renderEntertainment(el) {
    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">🎮 ${this.t('ترفيه','Entertainment')}</h2>

      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
        <div class="glass-card" style="padding: 22px; text-align: center; cursor: pointer;" onclick="nexusApp.navigate('chat-login')">
          <div style="font-size: 36px; margin-bottom: 8px;">💬</div>
          <div style="font-weight: 700; font-size: 15px;">${this.t('دردشة خاصة','Private Chat')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">Ahmed & Rody</div>
        </div>

        <div class="glass-card" style="padding: 22px; text-align: center; cursor: pointer;" onclick="nexusApp.navigate('games')">
          <div style="font-size: 36px; margin-bottom: 8px;">🎮</div>
          <div style="font-weight: 700; font-size: 15px;">${this.t('الألعاب','Games')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">4 ${this.t('ألعاب ثنائية','2P Games')}</div>
        </div>

        <div class="glass-card" style="padding: 22px; text-align: center; cursor: pointer;" onclick="nexusApp.navigate('islamic')">
          <div style="font-size: 36px; margin-bottom: 8px;">🕌</div>
          <div style="font-weight: 700; font-size: 15px;">${this.t('القسم الإسلامي','Islamic')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">${this.t('أذكار وقرآن ومواقيت','Azkar & Quran')}</div>
        </div>

        <div class="glass-card" style="padding: 22px; text-align: center; cursor: pointer;" onclick="nexusApp.navigate('mood')">
          <div style="font-size: 36px; margin-bottom: 8px;">😊</div>
          <div style="font-weight: 700; font-size: 15px;">${this.t('نفسيتي','My Mood')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">${this.t('شارك حالتك','Share mood')}</div>
        </div>
      </div>
    `;
  }

  // =========================================================
  // CHAT LOGIN
  // =========================================================
  renderChatLogin(el) {
    el.innerHTML = `
      <div style="display: flex; flex-direction: column; align-items: center; padding-top: 30px;">
        <div style="font-size: 56px; margin-bottom: 16px;">💬</div>
        <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 6px;">${this.t('الدردشة الخاصة','Private Chat')}</h2>
        <p style="color: var(--text-muted); font-size: 14px; margin-bottom: 24px;">${this.t('اختر حسابك وأدخل رمز الدخول','Choose your account')}</p>

        <!-- User Selection -->
        <div style="display: flex; gap: 14px; margin-bottom: 20px;">
          <button id="select-ahmed" class="glass-card" style="padding: 20px 28px; cursor: pointer; text-align: center; border: 2px solid transparent; transition: border-color 0.2s;">
            <div style="font-size: 36px; margin-bottom: 6px;">👨</div>
            <div style="font-weight: 700;">Ahmed</div>
          </button>
          <button id="select-rody" class="glass-card" style="padding: 20px 28px; cursor: pointer; text-align: center; border: 2px solid transparent; transition: border-color 0.2s;">
            <div style="font-size: 36px; margin-bottom: 6px;">👩</div>
            <div style="font-weight: 700;">Rody</div>
          </button>
        </div>

        <!-- Code Input -->
        <div id="code-section" style="display: none; width: 100%; max-width: 300px; text-align: center;">
          <input id="access-code-input" type="password" placeholder="${this.t('أدخل رمز الدخول','Enter access code')}" style="
            width: 100%; padding: 14px 18px; border-radius: 16px; border: 1px solid var(--surface-glass-border);
            background: var(--surface-glass); color: var(--text-main); font-size: 16px; text-align: center; outline: none; letter-spacing: 4px;
          ">
          <div id="login-error" style="color: var(--accent-pink); font-size: 13px; margin-top: 8px; display: none;"></div>
          <button id="chat-login-btn" class="glass-btn" style="margin-top: 14px; width: 100%; padding: 14px;">
            ${this.t('دخول الدردشة','Enter Chat')} 💬
          </button>
        </div>

        <button class="glass-btn-secondary" style="margin-top: 20px; padding: 8px 16px; font-size: 13px; cursor: pointer; border: 1px solid var(--surface-glass-border); border-radius: 12px; background: var(--surface-glass); color: var(--text-main);" onclick="nexusApp.navigate('entertainment')">
          ${this.t('رجوع','Back')} ↩️
        </button>
      </div>
    `;

    let selectedUser = null;

    const ahmedBtn = document.getElementById('select-ahmed');
    const rodyBtn = document.getElementById('select-rody');
    const codeSection = document.getElementById('code-section');

    const selectUser = (user) => {
      selectedUser = user;
      ahmedBtn.style.borderColor = user === 'ahmed' ? 'var(--primary)' : 'transparent';
      rodyBtn.style.borderColor = user === 'rody' ? 'var(--accent-pink)' : 'transparent';
      codeSection.style.display = 'block';
      document.getElementById('access-code-input').focus();
    };

    ahmedBtn.onclick = () => selectUser('ahmed');
    rodyBtn.onclick = () => selectUser('rody');

    document.getElementById('chat-login-btn')?.addEventListener('click', () => {
      const code = document.getElementById('access-code-input').value;
      const errorEl = document.getElementById('login-error');
      if (!selectedUser) { errorEl.textContent = this.t('اختر حسابك الأول','Select account first'); errorEl.style.display = 'block'; return; }
      if (realtimeStore.verifyCode(selectedUser, code)) {
        this.currentUser = selectedUser;
        this.gamesManager = new GamesManager(selectedUser, (userId, coins) => {
          // Coin earned callback
        });
        this.navigate('chat');
      } else {
        errorEl.textContent = this.t('رمز الدخول غلط، حاول تاني','Wrong code, try again');
        errorEl.style.display = 'block';
      }
    });
  }

  // =========================================================
  // PRIVATE CHAT
  // =========================================================
  renderChat(el) {
    if (!this.currentUser) { this.navigate('chat-login'); return; }
    const s = realtimeStore.state;
    const me = s.users[this.currentUser];
    const other = s.users[this.currentUser === 'ahmed' ? 'rody' : 'ahmed'];
    const msgs = s.messages;

    el.innerHTML = `
      <!-- Heart Counter -->
      <div id="heart-counter-area"></div>

      <!-- Other user's note -->
      ${other.note ? `
        <div class="glass-card" style="padding: 10px 14px; margin-bottom: 12px; font-size: 13px; display: flex; align-items: center; gap: 8px;">
          <span>📝</span>
          <span style="font-weight: 600;">${other.name}:</span>
          <span style="color: var(--text-muted);">${other.note}</span>
        </div>
      ` : ''}

      <!-- Other user's mood -->
      ${other.mood ? `
        <div class="glass-card" style="padding: 8px 14px; margin-bottom: 14px; font-size: 13px; display: flex; align-items: center; gap: 8px;">
          <span>${other.mood.emoji}</span>
          <span style="color: var(--text-muted);">${other.mood.text || ''}</span>
        </div>
      ` : ''}

      <!-- Chat Header -->
      <div class="glass-card" style="padding: 12px 16px; margin-bottom: 14px; display: flex; align-items: center; justify-content: space-between;">
        <div style="display: flex; align-items: center; gap: 10px;">
          <img src="${other.avatar}" alt="${other.name}" style="width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 2px solid var(--primary-light);">
          <div>
            <div style="font-weight: 700; font-size: 15px;">${other.name}</div>
            <div style="font-size: 11px; color: var(--accent-emerald);">● ${this.t('متصل','Online')}</div>
          </div>
        </div>
        <div style="display: flex; gap: 8px;">
          <button class="glass-btn-secondary" style="padding: 6px 10px; border-radius: 10px; font-size: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border);" onclick="nexusApp.navigate('bubble-styles')">🎨</button>
          <button class="glass-btn-secondary" style="padding: 6px 10px; border-radius: 10px; font-size: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border);" onclick="nexusApp.navigate('entertainment')">↩️</button>
        </div>
      </div>

      <!-- Messages -->
      <div id="chat-messages" style="display: flex; flex-direction: column; gap: 6px; margin-bottom: 14px; min-height: 200px;">
        ${msgs.length === 0 ? `
          <div style="text-align: center; padding: 40px 0; color: var(--text-muted);">
            <div style="font-size: 48px; margin-bottom: 8px;">💬</div>
            <div>${this.t('لسه مفيش رسائل هنا','No messages yet')} 💬</div>
          </div>
        ` : msgs.map(msg => {
          const isMe = msg.sender === this.currentUser;
          const senderData = s.users[msg.sender];
          const bubbleStyle = senderData?.bubbleStyle || 'default';
          const time = new Date(msg.timestamp).toLocaleTimeString('ar-EG', { hour: '2-digit', minute: '2-digit' });

          return `
            <div class="chat-msg ${isMe ? 'msg-ahmed' : 'msg-rody'} style-${bubbleStyle}" style="align-self: ${isMe ? 'flex-end' : 'flex-start'};">
              <div class="bubble-content">
                ${msg.type === 'image' ? `<img src="${msg.content}" class="chat-image-preview" alt="صورة">` :
                  msg.type === 'voice' ? `<div class="chat-audio-player"><audio controls src="${msg.content}" style="height: 32px;"></audio></div>` :
                  msg.content}
                ${msg.decoration ? `<span style="position: absolute; top: -6px; ${isMe ? 'left' : 'right'}: -4px; font-size: 12px;">${msg.decoration}</span>` : ''}
              </div>
              <div class="msg-meta">
                <span>${time}</span>
                ${isMe ? `<span>${msg.isRead ? '✓✓' : '✓'}</span>` : ''}
              </div>
            </div>
          `;
        }).join('')}
      </div>

      <!-- Message Input -->
      <div class="glass-panel" style="padding: 10px; display: flex; align-items: center; gap: 8px; position: sticky; bottom: 72px;">
        <label for="chat-image-input" style="cursor: pointer; font-size: 20px; padding: 4px;">📷</label>
        <input type="file" id="chat-image-input" accept="image/*" style="display: none;">
        <button id="chat-voice-btn" style="font-size: 20px; background: none; border: none; cursor: pointer; padding: 4px;">🎤</button>
        <input id="chat-text-input" type="text" placeholder="${this.t('اكتب رسالة...','Type a message...')}" style="
          flex: 1; padding: 10px 14px; border-radius: 14px; border: 1px solid var(--surface-glass-border);
          background: var(--surface-glass); color: var(--text-main); font-size: 14px; outline: none;
        ">
        <select id="chat-decoration-select" style="padding: 6px; border-radius: 10px; font-size: 14px; background: var(--surface-glass); border: 1px solid var(--surface-glass-border); color: var(--text-main);">
          <option value="">بدون</option>
          <option value="❤️">❤️</option>
          <option value="🌸">🌸</option>
          <option value="⭐">⭐</option>
          <option value="🐱">🐱</option>
          <option value="🐶">🐶</option>
        </select>
        <button id="chat-send-btn" class="glass-btn" style="padding: 10px 14px;">➤</button>
      </div>
    `;

    // Init Heart Counter
    const heartArea = document.getElementById('heart-counter-area');
    if (heartArea && s.chatCreatedAt) {
      if (this.heartCounter) this.heartCounter.destroy();
      this.heartCounter = new HeartCounter(s.chatCreatedAt, heartArea, (details) => {
        alert(`${this.t('مدة تواصلنا','Our connection')}:\n${details.totalDays} ${this.t('يوم','days')}\n${details.months} ${this.t('شهر','months')} و ${details.daysInMonth} ${this.t('يوم','days')}\n${details.hours}:${details.minutes}:${details.seconds}`);
      });
    }

    // Scroll to bottom
    const chatBox = document.getElementById('chat-messages');
    if (chatBox) chatBox.scrollTop = chatBox.scrollHeight;

    // Send text
    document.getElementById('chat-send-btn')?.addEventListener('click', () => {
      const input = document.getElementById('chat-text-input');
      const decoration = document.getElementById('chat-decoration-select')?.value || '';
      if (input && input.value.trim()) {
        realtimeStore.addMessage({
          sender: this.currentUser,
          type: 'text',
          content: input.value.trim(),
          decoration
        });
        input.value = '';
      }
    });

    // Send on Enter
    document.getElementById('chat-text-input')?.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') document.getElementById('chat-send-btn')?.click();
    });

    // Image upload
    document.getElementById('chat-image-input')?.addEventListener('change', (e) => {
      const file = e.target.files[0];
      if (!file) return;
      const reader = new FileReader();
      reader.onload = (ev) => {
        realtimeStore.addMessage({
          sender: this.currentUser,
          type: 'image',
          content: ev.target.result
        });
      };
      reader.readAsDataURL(file);
    });

    // Voice recording
    let mediaRecorder = null;
    let audioChunks = [];
    const voiceBtn = document.getElementById('chat-voice-btn');
    if (voiceBtn) {
      voiceBtn.onclick = async () => {
        if (mediaRecorder && mediaRecorder.state === 'recording') {
          mediaRecorder.stop();
          voiceBtn.textContent = '🎤';
          return;
        }
        try {
          const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
          mediaRecorder = new MediaRecorder(stream);
          audioChunks = [];
          mediaRecorder.ondataavailable = (e) => audioChunks.push(e.data);
          mediaRecorder.onstop = () => {
            const blob = new Blob(audioChunks, { type: 'audio/webm' });
            const url = URL.createObjectURL(blob);
            realtimeStore.addMessage({
              sender: this.currentUser,
              type: 'voice',
              content: url
            });
            stream.getTracks().forEach(t => t.stop());
          };
          mediaRecorder.start();
          voiceBtn.textContent = '⏹️';
        } catch (err) {
          alert(this.t('يرجى السماح باستخدام الميكروفون','Please allow microphone access'));
        }
      };
    }
  }

  // =========================================================
  // BUBBLE STYLES
  // =========================================================
  renderBubbleStyles(el) {
    if (!this.currentUser) { this.navigate('chat-login'); return; }
    const styles = [
      { id: 'default', name: this.t('كلاسيكي','Classic'), preview: '💎', cost: 0 },
      { id: 'flowers', name: this.t('زهور','Flowers'), preview: '🌸', cost: 0 },
      { id: 'hearts', name: this.t('قلوب','Hearts'), preview: '💖', cost: 0 },
      { id: 'cats', name: this.t('قطط','Cats'), preview: '🐱', cost: 30 },
      { id: 'dogs', name: this.t('كلاب','Dogs'), preview: '🐶', cost: 30 },
      { id: 'clouds', name: this.t('سحاب','Clouds'), preview: '☁️', cost: 50 },
      { id: 'stars', name: this.t('نجوم','Stars'), preview: '✨', cost: 50 }
    ];
    const currentStyle = realtimeStore.state.users[this.currentUser].bubbleStyle;
    const coins = realtimeStore.state.users[this.currentUser].coins;

    el.innerHTML = `
      <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px;">
        <h2 style="font-size: 20px; font-weight: 800;">🎨 ${this.t('أنماط الفقاعات','Bubble Styles')}</h2>
        <span class="glass-card" style="padding: 6px 14px; font-size: 13px; font-weight: 700;">🪙 ${coins}</span>
      </div>

      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
        ${styles.map(s => {
          const isActive = s.id === currentStyle;
          const canAfford = coins >= s.cost || s.cost === 0;
          return `
            <div class="glass-card" style="padding: 18px; text-align: center; border: 2px solid ${isActive ? 'var(--primary)' : 'transparent'};">
              <div style="font-size: 36px; margin-bottom: 6px;">${s.preview}</div>
              <div style="font-weight: 700; font-size: 14px; margin-bottom: 4px;">${s.name}</div>
              <div style="font-size: 12px; color: var(--text-muted); margin-bottom: 8px;">${s.cost === 0 ? this.t('مجاني','Free') : `🪙 ${s.cost}`}</div>
              <button class="glass-btn bubble-select-btn" data-style="${s.id}" data-cost="${s.cost}" style="padding: 6px 14px; font-size: 12px; width: 100%; ${isActive ? 'opacity: 0.6;' : ''}">
                ${isActive ? this.t('مُفعّل','Active') : (canAfford ? this.t('استخدم','Use') : this.t('غير كافي','Not enough'))}
              </button>
            </div>
          `;
        }).join('')}
      </div>
      <button class="glass-btn-secondary" style="margin-top: 16px; width: 100%; padding: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); border-radius: 14px; background: var(--surface-glass); color: var(--text-main);" onclick="nexusApp.navigate('chat')">
        ${this.t('رجوع للدردشة','Back to Chat')} ↩️
      </button>
    `;

    document.querySelectorAll('.bubble-select-btn').forEach(btn => {
      btn.onclick = () => {
        const styleId = btn.dataset.style;
        const cost = parseInt(btn.dataset.cost, 10);
        if (realtimeStore.state.users[this.currentUser].coins >= cost || cost === 0) {
          if (cost > 0) realtimeStore.adjustCoins(this.currentUser, -cost);
          realtimeStore.setBubbleStyle(this.currentUser, styleId);
          this.renderBubbleStyles(el);
        }
      };
    });
  }

  // =========================================================
  // GAMES HUB
  // =========================================================
  renderGames(el) {
    if (!this.currentUser) {
      // Need to login first to play
      el.innerHTML = `
        <div style="text-align: center; padding-top: 30px;">
          <div style="font-size: 56px; margin-bottom: 16px;">🎮</div>
          <h2 style="font-size: 20px; font-weight: 800; margin-bottom: 10px;">${this.t('سجل دخول أولاً','Login first')}</h2>
          <p style="color: var(--text-muted); margin-bottom: 20px;">${this.t('لازم تسجل دخول الدردشة الأول عشان تلعب','Login to chat first to play')}</p>
          <button class="glass-btn" onclick="nexusApp.navigate('chat-login')">${this.t('تسجيل دخول','Login')} 💬</button>
        </div>
      `;
      return;
    }

    const coins = realtimeStore.state.users[this.currentUser].coins;
    el.innerHTML = `
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h2 style="font-size: 22px; font-weight: 800;">🎮 ${this.t('الألعاب','Games')}</h2>
        <span class="glass-card" style="padding: 6px 14px; font-size: 13px; font-weight: 700;">🪙 ${coins}</span>
      </div>

      <!-- Game Tabs -->
      <div style="display: flex; gap: 8px; margin-bottom: 16px; overflow-x: auto; padding-bottom: 4px;">
        <button class="glass-btn game-tab-btn active" data-game="xo" style="flex-shrink: 0;">⭕ XO</button>
        <button class="glass-btn-secondary game-tab-btn" data-game="connect4" style="flex-shrink: 0; padding: 8px 14px; border-radius: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); background: var(--surface-glass); color: var(--text-main);">🔴 Connect 4</button>
        <button class="glass-btn-secondary game-tab-btn" data-game="racing" style="flex-shrink: 0; padding: 8px 14px; border-radius: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); background: var(--surface-glass); color: var(--text-main);">🏎️ ${this.t('سباق','Race')}</button>
        <button class="glass-btn-secondary game-tab-btn" data-game="quiz" style="flex-shrink: 0; padding: 8px 14px; border-radius: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); background: var(--surface-glass); color: var(--text-main);">❓ ${this.t('أسئلة','Quiz')}</button>
      </div>

      <div id="game-container"></div>

      <button class="glass-btn-secondary" style="margin-top: 16px; width: 100%; padding: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); border-radius: 14px; background: var(--surface-glass); color: var(--text-main);" onclick="nexusApp.navigate('entertainment')">
        ${this.t('رجوع','Back')} ↩️
      </button>
    `;

    const gameContainer = document.getElementById('game-container');
    this.gamesManager.initXO(gameContainer);

    document.querySelectorAll('.game-tab-btn').forEach(btn => {
      btn.onclick = () => {
        document.querySelectorAll('.game-tab-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        switch (btn.dataset.game) {
          case 'xo': this.gamesManager.initXO(gameContainer); break;
          case 'connect4': this.gamesManager.initConnectFour(gameContainer); break;
          case 'racing': this.gamesManager.initRacing(gameContainer); break;
          case 'quiz': this.gamesManager.initQuiz(gameContainer); break;
        }
      };
    });
  }

  // =========================================================
  // ISLAMIC SECTION
  // =========================================================
  renderIslamic(el) {
    const prayerTimes = PrayerTimesCalculator.getTimes();
    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">🕌 ${this.t('القسم الإسلامي','Islamic')}</h2>

      <!-- Prayer Times Full -->
      <div class="glass-card" style="padding: 18px; margin-bottom: 14px;">
        <h3 style="font-weight: 700; font-size: 16px; margin-bottom: 12px;">🕐 ${this.t('مواقيت الصلاة','Prayer Times')}</h3>
        ${[
          { name: this.t('الفجر','Fajr'), time: prayerTimes.fajr, emoji: '🌅' },
          { name: this.t('الشروق','Sunrise'), time: prayerTimes.sunrise, emoji: '☀️' },
          { name: this.t('الظهر','Dhuhr'), time: prayerTimes.dhuhr, emoji: '🌞' },
          { name: this.t('العصر','Asr'), time: prayerTimes.asr, emoji: '🌤️' },
          { name: this.t('المغرب','Maghrib'), time: prayerTimes.maghrib, emoji: '🌅' },
          { name: this.t('العشاء','Isha'), time: prayerTimes.isha, emoji: '🌙' }
        ].map(p => `
          <div style="display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid var(--surface-glass-border);">
            <span style="font-weight: 600;">${p.emoji} ${p.name}</span>
            <span style="font-weight: 700; color: var(--primary);">${p.time}</span>
          </div>
        `).join('')}
      </div>

      <!-- Quick Links -->
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
        <div class="glass-card" style="padding: 20px; text-align: center; cursor: pointer;" onclick="nexusApp.currentAzkarType='morning'; nexusApp.navigate('azkar');">
          <div style="font-size: 32px; margin-bottom: 6px;">🌅</div>
          <div style="font-weight: 700; font-size: 14px;">${this.t('أذكار الصباح','Morning Azkar')}</div>
        </div>
        <div class="glass-card" style="padding: 20px; text-align: center; cursor: pointer;" onclick="nexusApp.currentAzkarType='evening'; nexusApp.navigate('azkar');">
          <div style="font-size: 32px; margin-bottom: 6px;">🌙</div>
          <div style="font-weight: 700; font-size: 14px;">${this.t('أذكار المساء','Evening Azkar')}</div>
        </div>
        <div class="glass-card" style="padding: 20px; text-align: center; cursor: pointer; grid-column: span 2;" onclick="nexusApp.navigate('quran');">
          <div style="font-size: 32px; margin-bottom: 6px;">📖</div>
          <div style="font-weight: 700; font-size: 14px;">${this.t('القرآن الكريم','Holy Quran')}</div>
        </div>
      </div>

      <button class="glass-btn-secondary" style="margin-top: 16px; width: 100%; padding: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); border-radius: 14px; background: var(--surface-glass); color: var(--text-main);" onclick="nexusApp.navigate('entertainment')">
        ${this.t('رجوع','Back')} ↩️
      </button>
    `;
  }

  // =========================================================
  // AZKAR VIEWER
  // =========================================================
  renderAzkar(el) {
    const type = this.currentAzkarType || 'morning';
    const azkar = AZKAR_DATA[type] || [];
    const title = type === 'morning' ? this.t('أذكار الصباح','Morning Azkar') : this.t('أذكار المساء','Evening Azkar');

    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">${type === 'morning' ? '🌅' : '🌙'} ${title}</h2>
      ${azkar.map((z, i) => `
        <div class="glass-card" style="padding: 18px; margin-bottom: 12px;">
          <div style="font-size: 16px; line-height: 2; font-weight: 600; margin-bottom: 10px; text-align: center; font-family: 'Noto Sans Arabic', serif;">
            ${z.text}
          </div>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span style="font-size: 12px; color: var(--text-muted);">${z.virtue}</span>
            <div style="display: flex; align-items: center; gap: 8px;">
              <span class="azkar-counter" data-idx="${i}" style="
                padding: 6px 14px; border-radius: 12px; font-weight: 800; font-size: 14px; cursor: pointer;
                background: linear-gradient(135deg, var(--primary), var(--primary-dark));
                color: white; min-width: 40px; text-align: center;
              ">${z.count}</span>
            </div>
          </div>
        </div>
      `).join('')}
      <button class="glass-btn-secondary" style="margin-top: 10px; width: 100%; padding: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); border-radius: 14px; background: var(--surface-glass); color: var(--text-main);" onclick="nexusApp.navigate('islamic')">
        ${this.t('رجوع','Back')} ↩️
      </button>
    `;

    // Tasbeeh counter
    document.querySelectorAll('.azkar-counter').forEach(btn => {
      btn.onclick = () => {
        let count = parseInt(btn.textContent, 10);
        if (count > 0) {
          count--;
          btn.textContent = count;
          if (count === 0) {
            btn.style.background = 'var(--accent-emerald)';
            btn.textContent = '✓';
          }
        }
      };
    });
  }

  // =========================================================
  // QURAN VIEWER
  // =========================================================
  renderQuran(el) {
    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">📖 ${this.t('القرآن الكريم','Holy Quran')}</h2>
      <div class="glass-card" style="padding: 14px; margin-bottom: 14px;">
        <input id="quran-search" type="text" placeholder="${this.t('ابحث عن سورة...','Search surah...')}" style="
          width: 100%; padding: 10px 14px; border-radius: 12px; border: 1px solid var(--surface-glass-border);
          background: var(--surface-glass); color: var(--text-main); font-size: 14px; outline: none;
        ">
      </div>
      <div id="surah-list">
        ${SURAH_LIST.map(s => `
          <div class="glass-card surah-item" data-id="${s.id}" style="padding: 12px 16px; margin-bottom: 8px; display: flex; align-items: center; gap: 12px; cursor: pointer;">
            <div style="
              width: 36px; height: 36px; border-radius: 10px;
              background: linear-gradient(135deg, var(--primary-light), var(--primary));
              display: flex; align-items: center; justify-content: center;
              color: white; font-weight: 800; font-size: 13px; flex-shrink: 0;
            ">${s.id}</div>
            <div style="flex: 1;">
              <div style="font-weight: 700; font-size: 15px;">${s.name}</div>
              <div style="font-size: 12px; color: var(--text-muted);">${s.englishName} — ${s.verses} ${this.t('آية','verses')} — ${s.type}</div>
            </div>
          </div>
        `).join('')}
      </div>
      <button class="glass-btn-secondary" style="margin-top: 10px; width: 100%; padding: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); border-radius: 14px; background: var(--surface-glass); color: var(--text-main);" onclick="nexusApp.navigate('islamic')">
        ${this.t('رجوع','Back')} ↩️
      </button>
    `;

    // Search filter
    document.getElementById('quran-search')?.addEventListener('input', (e) => {
      const q = e.target.value.trim().toLowerCase();
      document.querySelectorAll('.surah-item').forEach(item => {
        const id = parseInt(item.dataset.id, 10);
        const surah = SURAH_LIST.find(s => s.id === id);
        const match = !q || surah.name.includes(q) || surah.englishName.toLowerCase().includes(q) || String(surah.id).includes(q);
        item.style.display = match ? 'flex' : 'none';
      });
    });

    // Surah click
    document.querySelectorAll('.surah-item').forEach(item => {
      item.onclick = async () => {
        const id = parseInt(item.dataset.id, 10);
        const surah = SURAH_LIST.find(s => s.id === id);
        el.innerHTML = `
          <div style="text-align: center; padding: 20px;">
            <div style="font-size: 24px;">⏳</div>
            <div>${this.t('جاري تحميل سورة','Loading Surah')} ${surah.name}...</div>
          </div>
        `;

        const verses = await fetchFullSurahText(id);
        if (!verses) {
          el.innerHTML = `<div class="glass-card" style="padding: 20px; text-align: center;">${this.t('تعذر تحميل السورة. تأكد من الاتصال بالإنترنت.','Could not load surah. Check your connection.')}</div>
          <button class="glass-btn" style="margin-top: 12px;" onclick="nexusApp.navigate('quran')">${this.t('رجوع','Back')}</button>`;
          return;
        }

        el.innerHTML = `
          <div style="margin-bottom: 16px;">
            <h2 style="font-size: 22px; font-weight: 800; text-align: center;">${surah.name}</h2>
            <div style="text-align: center; font-size: 13px; color: var(--text-muted);">${surah.englishName} — ${surah.verses} ${this.t('آية','verses')}</div>
          </div>

          <div class="glass-card" style="padding: 20px;">
            ${id !== 9 ? '<div style="text-align: center; font-size: 18px; font-weight: 600; margin-bottom: 16px; color: var(--primary);">بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ</div>' : ''}
            ${verses.map(v => `
              <span style="font-size: 18px; line-height: 2.2; font-family: 'Noto Sans Arabic', serif;">
                ${v.text} <span style="
                  display: inline-flex; align-items: center; justify-content: center;
                  width: 28px; height: 28px; border-radius: 50%;
                  background: linear-gradient(135deg, var(--primary-light), var(--primary));
                  color: white; font-size: 11px; font-weight: 700; margin: 0 2px;
                ">${v.number}</span>
              </span>
            `).join(' ')}
          </div>

          <button class="glass-btn-secondary" style="margin-top: 16px; width: 100%; padding: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); border-radius: 14px; background: var(--surface-glass); color: var(--text-main);" onclick="nexusApp.navigate('quran')">
            ${this.t('رجوع لقائمة السور','Back to Surah list')} ↩️
          </button>
        `;
      };
    });
  }

  // =========================================================
  // MOOD SECTION
  // =========================================================
  renderMood(el) {
    if (!this.currentUser) {
      el.innerHTML = `
        <div style="text-align: center; padding-top: 30px;">
          <div style="font-size: 56px; margin-bottom: 16px;">😊</div>
          <h2 style="font-size: 20px; font-weight: 800; margin-bottom: 10px;">${this.t('سجل دخول أولاً','Login first')}</h2>
          <button class="glass-btn" onclick="nexusApp.navigate('chat-login')">${this.t('تسجيل دخول','Login')} 💬</button>
        </div>
      `;
      return;
    }

    const s = realtimeStore.state;
    const me = s.users[this.currentUser];
    const other = s.users[this.currentUser === 'ahmed' ? 'rody' : 'ahmed'];
    const moods = [
      { emoji: '😊', label: this.t('مبسوط','Happy') },
      { emoji: '😢', label: this.t('تعبان','Tired') },
      { emoji: '😐', label: this.t('زهقان','Bored') },
      { emoji: '😔', label: this.t('مضايق','Upset') },
      { emoji: '😌', label: this.t('كويس','Fine') },
      { emoji: '🥳', label: this.t('فرحان','Excited') },
      { emoji: '😤', label: this.t('ملان','Frustrated') },
      { emoji: '😑', label: this.t('مش أحسن حاجة','Not great') }
    ];

    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">😊 ${this.t('نفسيتي','My Mood')}</h2>

      <!-- Other user mood -->
      <div class="glass-card" style="padding: 16px; margin-bottom: 16px;">
        <div style="font-weight: 700; font-size: 14px; margin-bottom: 6px;">${other.name} ${this.t('حاسس بإيه','feels')}</div>
        <div style="font-size: 28px; margin-bottom: 4px;">${other.mood?.emoji || '—'}</div>
        <div style="font-size: 13px; color: var(--text-muted);">${other.mood?.text || this.t('لسه ما حدد حالته','No mood set')}</div>
      </div>

      <!-- My mood selector -->
      <div class="glass-card" style="padding: 16px; margin-bottom: 14px;">
        <div style="font-weight: 700; font-size: 14px; margin-bottom: 10px;">${this.t('حالتك دلوقتي','Your mood now')}</div>
        <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; margin-bottom: 14px;">
          ${moods.map(m => `
            <button class="mood-btn glass-card" data-emoji="${m.emoji}" data-label="${m.label}" style="
              padding: 12px 4px; text-align: center; cursor: pointer; border: 2px solid ${me.mood?.emoji?.includes(m.emoji) ? 'var(--primary)' : 'transparent'};
            ">
              <div style="font-size: 28px;">${m.emoji}</div>
              <div style="font-size: 11px; margin-top: 4px;">${m.label}</div>
            </button>
          `).join('')}
        </div>
        <input id="mood-custom-text" type="text" placeholder="${this.t('اكتب جملة مخصصة...','Custom text...')}" value="${me.mood?.text || ''}" style="
          width: 100%; padding: 10px 14px; border-radius: 12px; border: 1px solid var(--surface-glass-border);
          background: var(--surface-glass); color: var(--text-main); font-size: 14px; outline: none;
        ">
      </div>

      <button class="glass-btn-secondary" style="width: 100%; padding: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); border-radius: 14px; background: var(--surface-glass); color: var(--text-main);" onclick="nexusApp.navigate('entertainment')">
        ${this.t('رجوع','Back')} ↩️
      </button>
    `;

    document.querySelectorAll('.mood-btn').forEach(btn => {
      btn.onclick = () => {
        const emoji = btn.dataset.emoji + ' ' + btn.dataset.label;
        const text = document.getElementById('mood-custom-text')?.value || '';
        realtimeStore.updateMood(this.currentUser, emoji, text);
        this.renderMood(el);
      };
    });
  }
}

// Global instance
window.nexusApp = new NexusApp();
