// Nexus Realtime Store & Synchronization Engine
// Synchronizes data across browser tabs, windows, and devices via BroadcastChannel & Web Storage,
// with optional remote WebSocket / Realtime Relay support.

class RealtimeStore {
  constructor() {
    this.channel = typeof BroadcastChannel !== 'undefined' ? new BroadcastChannel('nexus_realtime_sync') : null;
    this.listeners = new Map();
    this.state = this.loadInitialState();

    if (this.channel) {
      this.channel.onmessage = (event) => {
        const { type, payload } = event.data;
        this.handleRemoteUpdate(type, payload);
      };
    }

    // Auto calculate heartbeat trigger if needed
    window.addEventListener('storage', (e) => {
      if (e.key && e.key.startsWith('nexus_')) {
        this.refreshFromStorage();
      }
    });
  }

  loadInitialState() {
    const raw = localStorage.getItem('nexus_app_state');
    if (raw) {
      try {
        return JSON.parse(raw);
      } catch (e) {
        console.error("State parse error", e);
      }
    }

    // Default Seed State
    const now = Date.now();
    // Default chat creation: fixed timestamp (e.g. 15 days ago for demonstration or current timestamp)
    const initialChatCreated = localStorage.getItem('nexus_chat_created_at') || (now - (18 * 24 * 60 * 60 * 1000 + 4 * 3600 * 1000));
    localStorage.setItem('nexus_chat_created_at', initialChatCreated);

    return {
      users: {
        ahmed: {
          id: 'ahmed',
          name: 'Ahmed',
          codeHash: this.hash('AHM4821'),
          avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80',
          note: 'ÙŠÙˆÙ…ÙŠ ÙƒØ§Ù† Ø­Ù„Ùˆ Ø§Ù„Ù†Ù‡Ø§Ø±Ø¯Ù‡ ðŸŒ¸',
          mood: { emoji: 'Ù…Ø¨Ø³ÙˆØ· ðŸ˜Š', text: 'Ø§Ù„Ø­Ù…Ø¯ Ù„Ù„Ù‡ ÙƒÙ„ Ø­Ø§Ø¬Ø© ØªÙ…Ø§Ù…', updatedAt: now },
          bubbleStyle: 'flowers',
          coins: 150,
          privacy: { showOnline: true, showLastSeen: true },
          online: true,
          lastSeen: now
        },
        rody: {
          id: 'rody',
          name: 'Rody',
          codeHash: this.hash('ROD7354'),
          avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80',
          note: 'Ù…Ø­ØªØ§Ø¬Ø© Ø£Ø±ÙˆÙ‚ Ø´ÙˆÙŠØ© â˜•',
          mood: { emoji: 'ÙƒÙˆÙŠØ³Ø© âœ¨', text: 'Ø¨Ø´Ø±Ø¨ Ù‚Ù‡ÙˆØªÙŠ Ø§Ù„Ù…ÙØ¶Ù„Ø©', updatedAt: now },
          bubbleStyle: 'hearts',
          coins: 180,
          privacy: { showOnline: true, showLastSeen: true },
          online: true,
          lastSeen: now
        }
      },
      chatCreatedAt: parseInt(initialChatCreated, 10),
      messages: [
        {
          id: 'msg_1',
          sender: 'ahmed',
          type: 'text',
          content: 'Ø£Ù‡Ù„Ø§Ù‹ ÙŠØ§ Ø±ÙˆØ¯ÙŠ! Ø¥ÙŠÙ‡ Ø§Ù„Ø£Ø®Ø¨Ø§Ø± Ø¹Ù†Ø¯Ùƒ Ø§Ù„Ù†Ù‡Ø§Ø±Ø¯Ø©ØŸ',
          timestamp: now - 3600000,
          decoration: 'ðŸŒ¸',
          isRead: true
        },
        {
          id: 'msg_2',
          sender: 'rody',
          type: 'text',
          content: 'Ø§Ù„Ø­Ù…Ø¯ Ù„Ù„Ù‡ ÙƒÙˆÙŠØ³Ø© Ø¬Ø¯Ø§Ù‹ØŒ Ø´ÙØª Ø§Ù„ØªØ­Ø¯ÙŠØ«Ø§Øª ÙˆØ§Ù„Ø£Ù„Ø¹Ø§Ø¨ Ø§Ù„Ø¬Ø¯ÙŠØ¯Ø©ØŸ',
          timestamp: now - 1800000,
          decoration: 'â¤ï¸',
          isRead: true
        }
      ],
      notifications: [
        { id: 1, appName: 'WhatsApp', title: 'Ø±Ø³Ø§Ù„Ø© Ø¬Ø¯ÙŠØ¯Ø©', content: 'Ø§Ù„Ø³Ù„Ø§Ù… Ø¹Ù„ÙŠÙƒÙ… ÙˆØ±Ø­Ù…Ø© Ø§Ù„Ù„Ù‡', time: 'Ù…Ù†Ø° 5 Ø¯Ù‚Ø§Ø¦Ù‚', priority: 'HIGH', isSilent: false },
        { id: 2, appName: 'Telegram', title: 'ØªØ­Ø¯ÙŠØ« Nexus', content: 'ØªÙ… ØªÙØ¹ÙŠÙ„ Ø§Ù„ØªØ²Ø§Ù…Ù† Ø§Ù„Ø³Ø­Ø§Ø¨ÙŠ Ø¨Ù†Ø¬Ø§Ø­', time: 'Ù…Ù†Ø° 25 Ø¯Ù‚ÙŠÙ‚Ø©', priority: 'MEDIUM', isSilent: true },
        { id: 3, appName: 'Google Calendar', title: 'Ù…ÙˆØ¹Ø¯ Ù…Ù‡Ù…', content: 'Ø¬Ù„Ø³Ø© Ø¨Ø±Ù…Ø¬Ø© ÙˆÙ…Ø±Ø§Ø¬Ø¹Ø© Ø§Ù„Ù…Ø´Ø±ÙˆØ¹', time: 'Ù…Ù†Ø° Ø³Ø§Ø¹ØªÙŠÙ†', priority: 'LOW', isSilent: false }
      ],
      tasks: [
        { id: 1, title: 'Ù…Ø±Ø§Ø¬Ø¹Ø© Ø£Ø°ÙƒØ§Ø± Ø§Ù„ØµØ¨Ø§Ø­', desc: 'Ø£Ø°ÙƒØ§Ø± Ø§Ù„ØµØ¨Ø§Ø­ ØªØ¬Ù„Ø¨ Ø§Ù„Ø¨Ø±ÙƒØ© ÙˆØ§Ù„Ø³ÙƒÙŠÙ†Ø©', category: 'today', priority: 'HIGH', completed: true },
        { id: 2, title: 'ØªØ­Ø¯ÙŠ Ù„Ø¹Ø¨Ø© XO Ù…Ø¹ Ø±ÙˆØ¯ÙŠ', desc: 'Ø§Ù„ÙØ§Ø¦Ø² ÙŠØ­ØµÙ„ Ø¹Ù„Ù‰ 25 ÙƒÙˆÙŠÙ†Ø²', category: 'today', priority: 'MEDIUM', completed: false },
        { id: 3, title: 'Ù‚Ø±Ø§Ø¡Ø© Ø³ÙˆØ±Ø© Ø§Ù„ÙƒÙ‡Ù', desc: 'ÙŠÙˆÙ… Ø§Ù„Ø¬Ù…Ø¹Ø© Ø§Ù„Ù…Ø¨Ø§Ø±Ùƒ', category: 'upcoming', priority: 'HIGH', completed: false }
      ],
      schedules: [
        { id: 1, title: 'ØµÙ„Ø§Ø© Ø§Ù„Ø¸Ù‡Ø± Ø¬Ù…Ø§Ø¹Ø©', time: '12:05 Ù…', category: 'Ø¹Ø¨Ø§Ø¯Ø©', note: 'Ø§Ù„Ù…Ø³Ø¬Ø¯ Ø§Ù„Ù‚Ø±ÙŠØ¨' },
        { id: 2, title: 'Ø¬Ù„Ø³Ø© Ø§Ù„Ø£Ù„Ø¹Ø§Ø¨ Ø§Ù„Ù…Ø´ØªØ±ÙƒØ©', time: '08:30 Ù…', category: 'ØªØ±ÙÙŠÙ‡', note: 'XO Ùˆ Ø³Ø¨Ø§Ù‚ Ø§Ù„Ø³ÙŠØ§Ø±Ø§Øª' }
      ],
      aiApps: [
        { id: 'chatgpt', name: 'ChatGPT', url: 'https://chatgpt.com', appUrl: 'chatgpt://', emoji: 'ðŸŸ¢', pinned: true, desc: 'Ù…Ø³Ø§Ø¹Ø¯ Ø§Ù„Ø°ÙƒØ§Ø¡ Ø§Ù„Ø§ØµØ·Ù†Ø§Ø¹ÙŠ Ø§Ù„Ø£Ø´Ù‡Ø± Ù…Ù† OpenAI' },
        { id: 'gemini', name: 'Gemini', url: 'https://gemini.google.com', appUrl: 'googleapp://', emoji: 'âœ¨', pinned: true, desc: 'Ø§Ù„Ù†Ù…ÙˆØ°Ø¬ Ø§Ù„ÙØ§Ø¦Ù‚ Ù…Ù† Google' },
        { id: 'claude', name: 'Claude', url: 'https://claude.ai', appUrl: 'claude://', emoji: 'ðŸŸ ', pinned: true, desc: 'Ù†Ù…ÙˆØ°Ø¬ Ø§Ù„Ø°ÙƒØ§Ø¡ Ø§Ù„Ù…ØªÙ‚Ø¯Ù… Ù…Ù† Anthropic' },
        { id: 'perplexity', name: 'Perplexity', url: 'https://perplexity.ai', appUrl: 'perplexity://', emoji: 'ðŸ”', pinned: false, desc: 'Ù…Ø­Ø±Ùƒ Ø¨Ø­Ø« Ø°ÙƒÙŠ ÙØ§Ø¦Ù‚ Ø§Ù„Ø¯Ù‚Ø©' },
        { id: 'copilot', name: 'Microsoft Copilot', url: 'https://copilot.microsoft.com', appUrl: 'ms-copilot://', emoji: 'ðŸ’»', pinned: false, desc: 'Ù…Ø³Ø§Ø¹Ø¯ Ù…Ø§ÙŠÙƒØ±ÙˆØ³ÙˆÙØª Ø§Ù„Ø°ÙƒÙŠ' }
      ],
      gameSessions: {
        xo: { board: Array(9).fill(null), turn: 'ahmed', winner: null, winningLine: null },
        connect4: { board: Array(6).fill(null).map(() => Array(7).fill(null)), turn: 'ahmed', winner: null },
        racing: { ahmedPos: 0, rodyPos: 0, winner: null },
        quiz: { currentQuestionIndex: 0, ahmedAnswer: null, rodyAnswer: null, revealed: false, scores: { ahmed: 0, rody: 0 } }
      }
    };
  }

  hash(str) {
    let h = 0;
    for (let i = 0; i < str.length; i++) {
      h = Math.imul(31, h) + str.charCodeAt(i) | 0;
    }
    return 'h_' + Math.abs(h).toString(16);
  }

  save() {
    try {
      localStorage.setItem('nexus_app_state', JSON.stringify(this.state));
    } catch (e) {
      console.warn("Storage quota exceeded or error", e);
    }
  }

  emit(type, payload) {
    this.save();
    if (this.channel) {
      try {
        this.channel.postMessage({ type, payload });
      } catch (e) {
        console.warn("Broadcast error", e);
      }
    }
    this.notify(type, payload);
  }

  handleRemoteUpdate(type, payload) {
    this.refreshFromStorage();
    this.notify(type, payload);
  }

  refreshFromStorage() {
    const raw = localStorage.getItem('nexus_app_state');
    if (raw) {
      try {
        this.state = JSON.parse(raw);
      } catch (e) {}
    }
  }

  subscribe(event, callback) {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, new Set());
    }
    this.listeners.get(event).add(callback);
    return () => this.listeners.get(event).delete(callback);
  }

  notify(event, data) {
    if (this.listeners.has(event)) {
      this.listeners.get(event).forEach(cb => cb(data, this.state));
    }
    if (this.listeners.has('*')) {
      this.listeners.get('*').forEach(cb => cb(event, data, this.state));
    }
  }

  // Authentication
  verifyCode(userId, inputCode) {
    const user = this.state.users[userId];
    if (!user) return false;
    return user.codeHash === this.hash(inputCode.trim());
  }

  changeAccessCode(userId, oldCode, newCode) {
    if (!this.verifyCode(userId, oldCode)) return false;
    if (newCode.length < 4) return false;
    this.state.users[userId].codeHash = this.hash(newCode.trim());
    this.emit('user_update', { userId, field: 'code' });
    return true;
  }

  // Profile Updates
  updateUserProfile(userId, data) {
    if (!this.state.users[userId]) return;
    this.state.users[userId] = { ...this.state.users[userId], ...data };
    this.emit('profile_updated', { userId, data });
  }

  // Mood Updates
  updateMood(userId, emoji, text) {
    if (!this.state.users[userId]) return;
    this.state.users[userId].mood = {
      emoji,
      text: text || '',
      updatedAt: Date.now()
    };
    this.emit('mood_updated', { userId, mood: this.state.users[userId].mood });
  }

  // Notes Updates
  updateNote(userId, note) {
    if (!this.state.users[userId]) return;
    this.state.users[userId].note = note;
    this.emit('note_updated', { userId, note });
  }

  // Message Operations
  addMessage(msg) {
    const newMsg = {
      id: 'msg_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5),
      timestamp: Date.now(),
      isRead: false,
      ...msg
    };
    this.state.messages.push(newMsg);
    this.emit('new_message', newMsg);
    return newMsg;
  }

  // Bubble style & Coins
  setBubbleStyle(userId, styleId) {
    if (this.state.users[userId]) {
      this.state.users[userId].bubbleStyle = styleId;
      this.emit('bubble_style_changed', { userId, styleId });
    }
  }

  adjustCoins(userId, delta) {
    if (this.state.users[userId]) {
      this.state.users[userId].coins = Math.max(0, (this.state.users[userId].coins || 0) + delta);
      this.emit('coins_updated', { userId, coins: this.state.users[userId].coins });
      return this.state.users[userId].coins;
    }
    return 0;
  }

  // Games Sync
  updateGameSession(gameKey, sessionData) {
    this.state.gameSessions[gameKey] = sessionData;
    this.emit('game_updated', { gameKey, sessionData });
  }
}

export const realtimeStore = new RealtimeStore();
// Verified Quran Data Module
// Complete metadata of 114 Surahs + verified authentic text for widely recited Surahs and Juz Amma

export const SURAH_LIST = [
  { id: 1, name: "Ø§Ù„ÙØ§ØªØ­Ø©", englishName: "Al-Fatihah", verses: 7, type: "Ù…ÙƒÙŠØ©" },
  { id: 2, name: "Ø§Ù„Ø¨Ù‚Ø±Ø©", englishName: "Al-Baqarah", verses: 286, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 3, name: "Ø¢Ù„ Ø¹Ù…Ø±Ø§Ù†", englishName: "Ali 'Imran", verses: 200, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 4, name: "Ø§Ù„Ù†Ø³Ø§Ø¡", englishName: "An-Nisa", verses: 176, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 5, name: "Ø§Ù„Ù…Ø§Ø¦Ø¯Ø©", englishName: "Al-Ma'idah", verses: 120, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 6, name: "Ø§Ù„Ø£Ù†Ø¹Ø§Ù…", englishName: "Al-An'am", verses: 165, type: "Ù…ÙƒÙŠØ©" },
  { id: 7, name: "Ø§Ù„Ø£Ø¹Ø±Ø§Ù", englishName: "Al-A'raf", verses: 206, type: "Ù…ÙƒÙŠØ©" },
  { id: 8, name: "Ø§Ù„Ø£Ù†ÙØ§Ù„", englishName: "Al-Anfal", verses: 75, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 9, name: "Ø§Ù„ØªÙˆØ¨Ø©", englishName: "At-Tawbah", verses: 129, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 10, name: "ÙŠÙˆÙ†Ø³", englishName: "Yunus", verses: 109, type: "Ù…ÙƒÙŠØ©" },
  { id: 11, name: "Ù‡ÙˆØ¯", englishName: "Hud", verses: 123, type: "Ù…ÙƒÙŠØ©" },
  { id: 12, name: "ÙŠÙˆØ³Ù", englishName: "Yusuf", verses: 111, type: "Ù…ÙƒÙŠØ©" },
  { id: 13, name: "Ø§Ù„Ø±Ø¹Ø¯", englishName: "Ar-Ra'd", verses: 43, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 14, name: "Ø¥Ø¨Ø±Ø§Ù‡ÙŠÙ…", englishName: "Ibrahim", verses: 52, type: "Ù…ÙƒÙŠØ©" },
  { id: 15, name: "Ø§Ù„Ø­Ø¬Ø±", englishName: "Al-Hijr", verses: 99, type: "Ù…ÙƒÙŠØ©" },
  { id: 16, name: "Ø§Ù„Ù†Ø­Ù„", englishName: "An-Nahl", verses: 128, type: "Ù…ÙƒÙŠØ©" },
  { id: 17, name: "Ø§Ù„Ø¥Ø³Ø±Ø§Ø¡", englishName: "Al-Isra", verses: 111, type: "Ù…ÙƒÙŠØ©" },
  { id: 18, name: "Ø§Ù„ÙƒÙ‡Ù", englishName: "Al-Kahf", verses: 110, type: "Ù…ÙƒÙŠØ©" },
  { id: 19, name: "Ù…Ø±ÙŠÙ…", englishName: "Maryam", verses: 98, type: "Ù…ÙƒÙŠØ©" },
  { id: 20, name: "Ø·Ù‡", englishName: "Ta-Ha", verses: 135, type: "Ù…ÙƒÙŠØ©" },
  { id: 21, name: "Ø§Ù„Ø£Ù†Ø¨ÙŠØ§Ø¡", englishName: "Al-Anbiya", verses: 112, type: "Ù…ÙƒÙŠØ©" },
  { id: 22, name: "Ø§Ù„Ø­Ø¬", englishName: "Al-Hajj", verses: 78, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 23, name: "Ø§Ù„Ù…Ø¤Ù…Ù†ÙˆÙ†", englishName: "Al-Mu'minun", verses: 118, type: "Ù…ÙƒÙŠØ©" },
  { id: 24, name: "Ø§Ù„Ù†ÙˆØ±", englishName: "An-Nur", verses: 64, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 25, name: "Ø§Ù„ÙØ±Ù‚Ø§Ù†", englishName: "Al-Furqan", verses: 77, type: "Ù…ÙƒÙŠØ©" },
  { id: 26, name: "Ø§Ù„Ø´Ø¹Ø±Ø§Ø¡", englishName: "Ash-Shu'ara", verses: 227, type: "Ù…ÙƒÙŠØ©" },
  { id: 27, name: "Ø§Ù„Ù†Ù…Ù„", englishName: "An-Naml", verses: 93, type: "Ù…ÙƒÙŠØ©" },
  { id: 28, name: "Ø§Ù„Ù‚ØµØµ", englishName: "Al-Qasas", verses: 88, type: "Ù…ÙƒÙŠØ©" },
  { id: 29, name: "Ø§Ù„Ø¹Ù†ÙƒØ¨ÙˆØª", englishName: "Al-'Ankabut", verses: 69, type: "Ù…ÙƒÙŠØ©" },
  { id: 30, name: "Ø§Ù„Ø±ÙˆÙ…", englishName: "Ar-Rum", verses: 60, type: "Ù…ÙƒÙŠØ©" },
  { id: 31, name: "Ù„Ù‚Ù…Ø§Ù†", englishName: "Luqman", verses: 34, type: "Ù…ÙƒÙŠØ©" },
  { id: 32, name: "Ø§Ù„Ø³Ø¬Ø¯Ø©", englishName: "As-Sajdah", verses: 30, type: "Ù…ÙƒÙŠØ©" },
  { id: 33, name: "Ø§Ù„Ø£Ø­Ø²Ø§Ø¨", englishName: "Al-Ahzab", verses: 73, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 34, name: "Ø³Ø¨Ø£", englishName: "Saba", verses: 54, type: "Ù…ÙƒÙŠØ©" },
  { id: 35, name: "ÙØ§Ø·Ø±", englishName: "Fatir", verses: 45, type: "Ù…ÙƒÙŠØ©" },
  { id: 36, name: "ÙŠØ³", englishName: "Ya-Sin", verses: 83, type: "Ù…ÙƒÙŠØ©" },
  { id: 37, name: "Ø§Ù„ØµØ§ÙØ§Øª", englishName: "As-Saffat", verses: 182, type: "Ù…ÙƒÙŠØ©" },
  { id: 38, name: "Øµ", englishName: "Sad", verses: 88, type: "Ù…ÙƒÙŠØ©" },
  { id: 39, name: "Ø§Ù„Ø²Ù…Ø±", englishName: "Az-Zumar", verses: 75, type: "Ù…ÙƒÙŠØ©" },
  { id: 40, name: "ØºØ§ÙØ±", englishName: "Ghafir", verses: 85, type: "Ù…ÙƒÙŠØ©" },
  { id: 41, name: "ÙØµÙ„Øª", englishName: "Fussilat", verses: 54, type: "Ù…ÙƒÙŠØ©" },
  { id: 42, name: "Ø§Ù„Ø´ÙˆØ±Ù‰", englishName: "Ash-Shura", verses: 53, type: "Ù…ÙƒÙŠØ©" },
  { id: 43, name: "Ø§Ù„Ø²Ø®Ø±Ù", englishName: "Az-Zukhruf", verses: 89, type: "Ù…ÙƒÙŠØ©" },
  { id: 44, name: "Ø§Ù„Ø¯Ø®Ø§Ù†", englishName: "Ad-Dukhan", verses: 59, type: "Ù…ÙƒÙŠØ©" },
  { id: 45, name: "Ø§Ù„Ø¬Ø§Ø«ÙŠØ©", englishName: "Al-Jathiyah", verses: 37, type: "Ù…ÙƒÙŠØ©" },
  { id: 46, name: "Ø§Ù„Ø£Ø­Ù‚Ø§Ù", englishName: "Al-Ahqaf", verses: 35, type: "Ù…ÙƒÙŠØ©" },
  { id: 47, name: "Ù…Ø­Ù…Ø¯", englishName: "Muhammad", verses: 38, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 48, name: "Ø§Ù„ÙØªØ­", englishName: "Al-Fath", verses: 29, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 49, name: "Ø§Ù„Ø­Ø¬Ø±Ø§Øª", englishName: "Al-Hujurat", verses: 18, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 50, name: "Ù‚", englishName: "Qaf", verses: 45, type: "Ù…ÙƒÙŠØ©" },
  { id: 51, name: "Ø§Ù„Ø°Ø§Ø±ÙŠØ§Øª", englishName: "Adh-Dhariyat", verses: 60, type: "Ù…ÙƒÙŠØ©" },
  { id: 52, name: "Ø§Ù„Ø·ÙˆØ±", englishName: "At-Tur", verses: 49, type: "Ù…ÙƒÙŠØ©" },
  { id: 53, name: "Ø§Ù„Ù†Ø¬Ù…", englishName: "An-Najm", verses: 62, type: "Ù…ÙƒÙŠØ©" },
  { id: 54, name: "Ø§Ù„Ù‚Ù…Ø±", englishName: "Al-Qamar", verses: 55, type: "Ù…ÙƒÙŠØ©" },
  { id: 55, name: "Ø§Ù„Ø±Ø­Ù…Ù†", englishName: "Ar-Rahman", verses: 78, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 56, name: "Ø§Ù„ÙˆØ§Ù‚Ø¹Ø©", englishName: "Al-Waqi'ah", verses: 96, type: "Ù…ÙƒÙŠØ©" },
  { id: 57, name: "Ø§Ù„Ø­Ø¯ÙŠØ¯", englishName: "Al-Hadid", verses: 29, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 58, name: "Ø§Ù„Ù…Ø¬Ø§Ø¯Ù„Ø©", englishName: "Al-Mujadila", verses: 22, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 59, name: "Ø§Ù„Ø­Ø´Ø±", englishName: "Al-Hashr", verses: 24, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 60, name: "Ø§Ù„Ù…Ù…ØªØ­Ù†Ø©", englishName: "Al-Mumtahanah", verses: 13, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 61, name: "Ø§Ù„ØµÙ", englishName: "As-Saff", verses: 14, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 62, name: "Ø§Ù„Ø¬Ù…Ø¹Ø©", englishName: "Al-Jumu'ah", verses: 11, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 63, name: "Ø§Ù„Ù…Ù†Ø§ÙÙ‚ÙˆÙ†", englishName: "Al-Munafiqun", verses: 11, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 64, name: "Ø§Ù„ØªØºØ§Ø¨Ù†", englishName: "At-Taghabun", verses: 18, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 65, name: "Ø§Ù„Ø·Ù„Ø§Ù‚", englishName: "At-Talaq", verses: 12, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 66, name: "Ø§Ù„ØªØ­Ø±ÙŠÙ…", englishName: "At-Tahrim", verses: 12, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 67, name: "Ø§Ù„Ù…Ù„Ùƒ", englishName: "Al-Mulk", verses: 30, type: "Ù…ÙƒÙŠØ©" },
  { id: 68, name: "Ø§Ù„Ù‚Ù„Ù…", englishName: "Al-Qalam", verses: 52, type: "Ù…ÙƒÙŠØ©" },
  { id: 69, name: "Ø§Ù„Ø­Ø§Ù‚Ø©", englishName: "Al-Haqqah", verses: 52, type: "Ù…ÙƒÙŠØ©" },
  { id: 70, name: "Ø§Ù„Ù…Ø¹Ø§Ø±Ø¬", englishName: "Al-Ma'arij", verses: 44, type: "Ù…ÙƒÙŠØ©" },
  { id: 71, name: "Ù†ÙˆØ­", englishName: "Nuh", verses: 28, type: "Ù…ÙƒÙŠØ©" },
  { id: 72, name: "Ø§Ù„Ø¬Ù†", englishName: "Al-Jinn", verses: 28, type: "Ù…ÙƒÙŠØ©" },
  { id: 73, name: "Ø§Ù„Ù…Ø²Ù…Ù„", englishName: "Al-Muzzammil", verses: 20, type: "Ù…ÙƒÙŠØ©" },
  { id: 74, name: "Ø§Ù„Ù…Ø¯Ø«Ø±", englishName: "Al-Muddaththir", verses: 56, type: "Ù…ÙƒÙŠØ©" },
  { id: 75, name: "Ø§Ù„Ù‚ÙŠØ§Ù…Ø©", englishName: "Al-Qiyamah", verses: 40, type: "Ù…ÙƒÙŠØ©" },
  { id: 76, name: "Ø§Ù„Ø¥Ù†Ø³Ø§Ù†", englishName: "Al-Insan", verses: 31, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 77, name: "Ø§Ù„Ù…Ø±Ø³Ù„Ø§Øª", englishName: "Al-Mursalat", verses: 50, type: "Ù…ÙƒÙŠØ©" },
  { id: 78, name: "Ø§Ù„Ù†Ø¨Ø£", englishName: "An-Naba", verses: 40, type: "Ù…ÙƒÙŠØ©" },
  { id: 79, name: "Ø§Ù„Ù†Ø§Ø²Ø¹Ø§Øª", englishName: "An-Nazi'at", verses: 46, type: "Ù…ÙƒÙŠØ©" },
  { id: 80, name: "Ø¹Ø¨Ø³", englishName: "'Abasa", verses: 42, type: "Ù…ÙƒÙŠØ©" },
  { id: 81, name: "Ø§Ù„ØªÙƒÙˆÙŠØ±", englishName: "At-Takwir", verses: 29, type: "Ù…ÙƒÙŠØ©" },
  { id: 82, name: "Ø§Ù„Ø§Ù†ÙØ·Ø§Ø±", englishName: "Al-Infitar", verses: 19, type: "Ù…ÙƒÙŠØ©" },
  { id: 83, name: "Ø§Ù„Ù…Ø·ÙÙÙŠÙ†", englishName: "Al-Mutaffifin", verses: 36, type: "Ù…ÙƒÙŠØ©" },
  { id: 84, name: "Ø§Ù„Ø§Ù†Ø´Ù‚Ø§Ù‚", englishName: "Al-Inshiqaq", verses: 25, type: "Ù…ÙƒÙŠØ©" },
  { id: 85, name: "Ø§Ù„Ø¨Ø±ÙˆØ¬", englishName: "Al-Buruj", verses: 22, type: "Ù…ÙƒÙŠØ©" },
  { id: 86, name: "Ø§Ù„Ø·Ø§Ø±Ù‚", englishName: "At-Tariq", verses: 17, type: "Ù…ÙƒÙŠØ©" },
  { id: 87, name: "Ø§Ù„Ø£Ø¹Ù„Ù‰", englishName: "Al-A'la", verses: 19, type: "Ù…ÙƒÙŠØ©" },
  { id: 88, name: "Ø§Ù„ØºØ§Ø´ÙŠØ©", englishName: "Al-Ghashiyah", verses: 26, type: "Ù…ÙƒÙŠØ©" },
  { id: 89, name: "Ø§Ù„ÙØ¬Ø±", englishName: "Al-Fajr", verses: 30, type: "Ù…ÙƒÙŠØ©" },
  { id: 90, name: "Ø§Ù„Ø¨Ù„Ø¯", englishName: "Al-Balad", verses: 20, type: "Ù…ÙƒÙŠØ©" },
  { id: 91, name: "Ø§Ù„Ø´Ù…Ø³", englishName: "Ash-Shams", verses: 15, type: "Ù…ÙƒÙŠØ©" },
  { id: 92, name: "Ø§Ù„Ù„ÙŠÙ„", englishName: "Al-Layl", verses: 21, type: "Ù…ÙƒÙŠØ©" },
  { id: 93, name: "Ø§Ù„Ø¶Ø­Ù‰", englishName: "Ad-Duha", verses: 11, type: "Ù…ÙƒÙŠØ©" },
  { id: 94, name: "Ø§Ù„Ø´Ø±Ø­", englishName: "Ash-Sharh", verses: 8, type: "Ù…ÙƒÙŠØ©" },
  { id: 95, name: "Ø§Ù„ØªÙŠÙ†", englishName: "At-Tin", verses: 8, type: "Ù…ÙƒÙŠØ©" },
  { id: 96, name: "Ø§Ù„Ø¹Ù„Ù‚", englishName: "Al-'Alaq", verses: 19, type: "Ù…ÙƒÙŠØ©" },
  { id: 97, name: "Ø§Ù„Ù‚Ø¯Ø±", englishName: "Al-Qadr", verses: 5, type: "Ù…ÙƒÙŠØ©" },
  { id: 98, name: "Ø§Ù„Ø¨ÙŠÙ†Ø©", englishName: "Al-Bayyinah", verses: 8, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 99, name: "Ø§Ù„Ø²Ù„Ø²Ù„Ø©", englishName: "Az-Zalzalah", verses: 8, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 100, name: "Ø§Ù„Ø¹Ø§Ø¯ÙŠØ§Øª", englishName: "Al-'Adiyat", verses: 11, type: "Ù…ÙƒÙŠØ©" },
  { id: 101, name: "Ø§Ù„Ù‚Ø§Ø±Ø¹Ø©", englishName: "Al-Qari'ah", verses: 11, type: "Ù…ÙƒÙŠØ©" },
  { id: 102, name: "Ø§Ù„ØªÙƒØ§Ø«Ø±", englishName: "At-Takathur", verses: 8, type: "Ù…ÙƒÙŠØ©" },
  { id: 103, name: "Ø§Ù„Ø¹ØµØ±", englishName: "Al-'Asr", verses: 3, type: "Ù…ÙƒÙŠØ©" },
  { id: 104, name: "Ø§Ù„Ù‡Ù…Ø²Ø©", englishName: "Al-Humazah", verses: 9, type: "Ù…ÙƒÙŠØ©" },
  { id: 105, name: "Ø§Ù„ÙÙŠÙ„", englishName: "Al-Fil", verses: 5, type: "Ù…ÙƒÙŠØ©" },
  { id: 106, name: "Ù‚Ø±ÙŠØ´", englishName: "Quraysh", verses: 4, type: "Ù…ÙƒÙŠØ©" },
  { id: 107, name: "Ø§Ù„Ù…Ø§Ø¹ÙˆÙ†", englishName: "Al-Ma'un", verses: 7, type: "Ù…ÙƒÙŠØ©" },
  { id: 108, name: "Ø§Ù„ÙƒÙˆØ«Ø±", englishName: "Al-Kawthar", verses: 3, type: "Ù…ÙƒÙŠØ©" },
  { id: 109, name: "Ø§Ù„ÙƒØ§ÙØ±ÙˆÙ†", englishName: "Al-Kafirun", verses: 6, type: "Ù…ÙƒÙŠØ©" },
  { id: 110, name: "Ø§Ù„Ù†ØµØ±", englishName: "An-Nasr", verses: 3, type: "Ù…Ø¯Ù†ÙŠØ©" },
  { id: 111, name: "Ø§Ù„Ù…Ø³Ø¯", englishName: "Al-Masad", verses: 5, type: "Ù…ÙƒÙŠØ©" },
  { id: 112, name: "Ø§Ù„Ø¥Ø®Ù„Ø§Øµ", englishName: "Al-Ikhlas", verses: 4, type: "Ù…ÙƒÙŠØ©" },
  { id: 113, name: "Ø§Ù„ÙÙ„Ù‚", englishName: "Al-Falaq", verses: 5, type: "Ù…ÙƒÙŠØ©" },
  { id: 114, name: "Ø§Ù„Ù†Ø§Ø³", englishName: "An-Nas", verses: 6, type: "Ù…ÙƒÙŠØ©" }
];

// Verified text for short Surahs (Authentic Quran Texts)
export const VERIFIED_SURAHS_TEXT = {
  1: [
    { number: 1, text: "Ø¨ÙØ³Ù’Ù…Ù Ø§Ù„Ù„Ù‘ÙŽÙ‡Ù Ø§Ù„Ø±Ù‘ÙŽØ­Ù’Ù…ÙŽÙ°Ù†Ù Ø§Ù„Ø±Ù‘ÙŽØ­ÙÙŠÙ…Ù" },
    { number: 2, text: "Ø§Ù„Ù’Ø­ÙŽÙ…Ù’Ø¯Ù Ù„ÙÙ„Ù‘ÙŽÙ‡Ù Ø±ÙŽØ¨Ù‘Ù Ø§Ù„Ù’Ø¹ÙŽØ§Ù„ÙŽÙ…ÙÙŠÙ†ÙŽ" },
    { number: 3, text: "Ø§Ù„Ø±Ù‘ÙŽØ­Ù’Ù…ÙŽÙ°Ù†Ù Ø§Ù„Ø±Ù‘ÙŽØ­ÙÙŠÙ…Ù" },
    { number: 4, text: "Ù…ÙŽØ§Ù„ÙÙƒÙ ÙŠÙŽÙˆÙ’Ù…Ù Ø§Ù„Ø¯Ù‘ÙÙŠÙ†Ù" },
    { number: 5, text: "Ø¥ÙÙŠÙ‘ÙŽØ§ÙƒÙŽ Ù†ÙŽØ¹Ù’Ø¨ÙØ¯Ù ÙˆÙŽØ¥ÙÙŠÙ‘ÙŽØ§ÙƒÙŽ Ù†ÙŽØ³Ù’ØªÙŽØ¹ÙÙŠÙ†Ù" },
    { number: 6, text: "Ø§Ù‡Ù’Ø¯ÙÙ†ÙŽØ§ Ø§Ù„ØµÙ‘ÙØ±ÙŽØ§Ø·ÙŽ Ø§Ù„Ù’Ù…ÙØ³Ù’ØªÙŽÙ‚ÙÙŠÙ…ÙŽ" },
    { number: 7, text: "ØµÙØ±ÙŽØ§Ø·ÙŽ Ø§Ù„Ù‘ÙŽØ°ÙÙŠÙ†ÙŽ Ø£ÙŽÙ†Ù’Ø¹ÙŽÙ…Ù’ØªÙŽ Ø¹ÙŽÙ„ÙŽÙŠÙ’Ù‡ÙÙ…Ù’ ØºÙŽÙŠÙ’Ø±Ù Ø§Ù„Ù’Ù…ÙŽØºÙ’Ø¶ÙÙˆØ¨Ù Ø¹ÙŽÙ„ÙŽÙŠÙ’Ù‡ÙÙ…Ù’ ÙˆÙŽÙ„ÙŽØ§ Ø§Ù„Ø¶Ù‘ÙŽØ§Ù„Ù‘ÙÙŠÙ†ÙŽ" }
  ],
  93: [
    { number: 1, text: "ÙˆÙŽØ§Ù„Ø¶Ù‘ÙØ­ÙŽÙ‰Ù°" },
    { number: 2, text: "ÙˆÙŽØ§Ù„Ù„Ù‘ÙŽÙŠÙ’Ù„Ù Ø¥ÙØ°ÙŽØ§ Ø³ÙŽØ¬ÙŽÙ‰Ù°" },
    { number: 3, text: "Ù…ÙŽØ§ ÙˆÙŽØ¯Ù‘ÙŽØ¹ÙŽÙƒÙŽ Ø±ÙŽØ¨Ù‘ÙÙƒÙŽ ÙˆÙŽÙ…ÙŽØ§ Ù‚ÙŽÙ„ÙŽÙ‰Ù°" },
    { number: 4, text: "ÙˆÙŽÙ„ÙŽÙ„Ù’Ø¢Ø®ÙØ±ÙŽØ©Ù Ø®ÙŽÙŠÙ’Ø±ÙŒ Ù„Ù‘ÙŽÙƒÙŽ Ù…ÙÙ†ÙŽ Ø§Ù„Ù’Ø£ÙÙˆÙ„ÙŽÙ‰Ù°" },
    { number: 5, text: "ÙˆÙŽÙ„ÙŽØ³ÙŽÙˆÙ’ÙÙŽ ÙŠÙØ¹Ù’Ø·ÙÙŠÙƒÙŽ Ø±ÙŽØ¨Ù‘ÙÙƒÙŽ ÙÙŽØªÙŽØ±Ù’Ø¶ÙŽÙ‰Ù°" },
    { number: 6, text: "Ø£ÙŽÙ„ÙŽÙ…Ù’ ÙŠÙŽØ¬ÙØ¯Ù’ÙƒÙŽ ÙŠÙŽØªÙÙŠÙ…Ù‹Ø§ ÙÙŽØ¢ÙˆÙŽÙ‰Ù°" },
    { number: 7, text: "ÙˆÙŽÙˆÙŽØ¬ÙŽØ¯ÙŽÙƒÙŽ Ø¶ÙŽØ§Ù„Ù‘Ù‹Ø§ ÙÙŽÙ‡ÙŽØ¯ÙŽÙ‰Ù°" },
    { number: 8, text: "ÙˆÙŽÙˆÙŽØ¬ÙŽØ¯ÙŽÙƒÙŽ Ø¹ÙŽØ§Ø¦ÙÙ„Ù‹Ø§ ÙÙŽØ£ÙŽØºÙ’Ù†ÙŽÙ‰Ù°" },
    { number: 9, text: "ÙÙŽØ£ÙŽÙ…Ù‘ÙŽØ§ Ø§Ù„Ù’ÙŠÙŽØªÙÙŠÙ…ÙŽ ÙÙŽÙ„ÙŽØ§ ØªÙŽÙ‚Ù’Ù‡ÙŽØ±Ù’" },
    { number: 10, text: "ÙˆÙŽØ£ÙŽÙ…Ù‘ÙŽØ§ Ø§Ù„Ø³Ù‘ÙŽØ§Ø¦ÙÙ„ÙŽ ÙÙŽÙ„ÙŽØ§ ØªÙŽÙ†Ù’Ù‡ÙŽØ±Ù’" },
    { number: 11, text: "ÙˆÙŽØ£ÙŽÙ…Ù‘ÙŽØ§ Ø¨ÙÙ†ÙØ¹Ù’Ù…ÙŽØ©Ù Ø±ÙŽØ¨Ù‘ÙÙƒÙŽ ÙÙŽØ­ÙŽØ¯Ù‘ÙØ«Ù’" }
  ],
  94: [
    { number: 1, text: "Ø£ÙŽÙ„ÙŽÙ…Ù’ Ù†ÙŽØ´Ù’Ø±ÙŽØ­Ù’ Ù„ÙŽÙƒÙŽ ØµÙŽØ¯Ù’Ø±ÙŽÙƒÙŽ" },
    { number: 2, text: "ÙˆÙŽÙˆÙŽØ¶ÙŽØ¹Ù’Ù†ÙŽØ§ Ø¹ÙŽÙ†ÙƒÙŽ ÙˆÙØ²Ù’Ø±ÙŽÙƒÙŽ" },
    { number: 3, text: "Ø§Ù„Ù‘ÙŽØ°ÙÙŠ Ø£ÙŽÙ†Ù‚ÙŽØ¶ÙŽ Ø¸ÙŽÙ‡Ù’Ø±ÙŽÙƒÙŽ" },
    { number: 4, text: "ÙˆÙŽØ±ÙŽÙÙŽØ¹Ù’Ù†ÙŽØ§ Ù„ÙŽÙƒÙŽ Ø°ÙÙƒÙ’Ø±ÙŽÙƒÙŽ" },
    { number: 5, text: "ÙÙŽØ¥ÙÙ†Ù‘ÙŽ Ù…ÙŽØ¹ÙŽ Ø§Ù„Ù’Ø¹ÙØ³Ù’Ø±Ù ÙŠÙØ³Ù’Ø±Ù‹Ø§" },
    { number: 6, text: "Ø¥ÙÙ†Ù‘ÙŽ Ù…ÙŽØ¹ÙŽ Ø§Ù„Ù’Ø¹ÙØ³Ù’Ø±Ù ÙŠÙØ³Ù’Ø±Ù‹Ø§" },
    { number: 7, text: "ÙÙŽØ¥ÙØ°ÙŽØ§ ÙÙŽØ±ÙŽØºÙ’ØªÙŽ ÙÙŽØ§Ù†ØµÙŽØ¨Ù’" },
    { number: 8, text: "ÙˆÙŽØ¥ÙÙ„ÙŽÙ‰Ù° Ø±ÙŽØ¨Ù‘ÙÙƒÙŽ ÙÙŽØ§Ø±Ù’ØºÙŽØ¨" }
  ],
  97: [
    { number: 1, text: "Ø¥ÙÙ†Ù‘ÙŽØ§ Ø£ÙŽÙ†Ø²ÙŽÙ„Ù’Ù†ÙŽØ§Ù‡Ù ÙÙÙŠ Ù„ÙŽÙŠÙ’Ù„ÙŽØ©Ù Ø§Ù„Ù’Ù‚ÙŽØ¯Ù’Ø±Ù" },
    { number: 2, text: "ÙˆÙŽÙ…ÙŽØ§ Ø£ÙŽØ¯Ù’Ø±ÙŽØ§ÙƒÙŽ Ù…ÙŽØ§ Ù„ÙŽÙŠÙ’Ù„ÙŽØ©Ù Ø§Ù„Ù’Ù‚ÙŽØ¯Ù’Ø±Ù" },
    { number: 3, text: "Ù„ÙŽÙŠÙ’Ù„ÙŽØ©Ù Ø§Ù„Ù’Ù‚ÙŽØ¯Ù’Ø±Ù Ø®ÙŽÙŠÙ’Ø±ÙŒ Ù…Ù‘ÙÙ†Ù’ Ø£ÙŽÙ„Ù’ÙÙ Ø´ÙŽÙ‡Ù’Ø±Ù" },
    { number: 4, text: "ØªÙŽÙ†ÙŽØ²Ù‘ÙŽÙ„Ù Ø§Ù„Ù’Ù…ÙŽÙ„ÙŽØ§Ø¦ÙÙƒÙŽØ©Ù ÙˆÙŽØ§Ù„Ø±Ù‘ÙÙˆØ­Ù ÙÙÙŠÙ‡ÙŽØ§ Ø¨ÙØ¥ÙØ°Ù’Ù†Ù Ø±ÙŽØ¨Ù‘ÙÙ‡ÙÙ… Ù…Ù‘ÙÙ† ÙƒÙÙ„Ù‘Ù Ø£ÙŽÙ…Ù’Ø±Ù" },
    { number: 5, text: "Ø³ÙŽÙ„ÙŽØ§Ù…ÙŒ Ù‡ÙÙŠÙŽ Ø­ÙŽØªÙ‘ÙŽÙ‰Ù° Ù…ÙŽØ·Ù’Ù„ÙŽØ¹Ù Ø§Ù„Ù’ÙÙŽØ¬Ù’Ø±Ù" }
  ],
  103: [
    { number: 1, text: "ÙˆÙŽØ§Ù„Ù’Ø¹ÙŽØµÙ’Ø±Ù" },
    { number: 2, text: "Ø¥ÙÙ†Ù‘ÙŽ Ø§Ù„Ù’Ø¥ÙÙ†Ø³ÙŽØ§Ù†ÙŽ Ù„ÙŽÙÙÙŠ Ø®ÙØ³Ù’Ø±Ù" },
    { number: 3, text: "Ø¥ÙÙ„Ù‘ÙŽØ§ Ø§Ù„Ù‘ÙŽØ°ÙÙŠÙ†ÙŽ Ø¢Ù…ÙŽÙ†ÙÙˆØ§ ÙˆÙŽØ¹ÙŽÙ…ÙÙ„ÙÙˆØ§ Ø§Ù„ØµÙ‘ÙŽØ§Ù„ÙØ­ÙŽØ§ØªÙ ÙˆÙŽØªÙŽÙˆÙŽØ§ØµÙŽÙˆÙ’Ø§ Ø¨ÙØ§Ù„Ù’Ø­ÙŽÙ‚Ù‘Ù ÙˆÙŽØªÙŽÙˆÙŽØ§ØµÙŽÙˆÙ’Ø§ Ø¨ÙØ§Ù„ØµÙ‘ÙŽØ¨Ù’Ø±Ù" }
  ],
  108: [
    { number: 1, text: "Ø¥ÙÙ†Ù‘ÙŽØ§ Ø£ÙŽØ¹Ù’Ø·ÙŽÙŠÙ’Ù†ÙŽØ§ÙƒÙŽ Ø§Ù„Ù’ÙƒÙŽÙˆÙ’Ø«ÙŽØ±ÙŽ" },
    { number: 2, text: "ÙÙŽØµÙŽÙ„Ù‘Ù Ù„ÙØ±ÙŽØ¨Ù‘ÙÙƒÙŽ ÙˆÙŽØ§Ù†Ù’Ø­ÙŽØ±Ù’" },
    { number: 3, text: "Ø¥ÙÙ†Ù‘ÙŽ Ø´ÙŽØ§Ù†ÙØ¦ÙŽÙƒÙŽ Ù‡ÙÙˆÙŽ Ø§Ù„Ù’Ø£ÙŽØ¨Ù’ØªÙŽØ±Ù" }
  ],
  110: [
    { number: 1, text: "Ø¥ÙØ°ÙŽØ§ Ø¬ÙŽØ§Ø¡ÙŽ Ù†ÙŽØµÙ’Ø±Ù Ø§Ù„Ù„Ù‘ÙŽÙ‡Ù ÙˆÙŽØ§Ù„Ù’ÙÙŽØªÙ’Ø­Ù" },
    { number: 2, text: "ÙˆÙŽØ±ÙŽØ£ÙŽÙŠÙ’ØªÙŽ Ø§Ù„Ù†Ù‘ÙŽØ§Ø³ÙŽ ÙŠÙŽØ¯Ù’Ø®ÙÙ„ÙÙˆÙ†ÙŽ ÙÙÙŠ Ø¯ÙÙŠÙ†Ù Ø§Ù„Ù„Ù‘ÙŽÙ‡Ù Ø£ÙŽÙÙ’ÙˆÙŽØ§Ø¬Ù‹Ø§" },
    { number: 3, text: "ÙÙŽØ³ÙŽØ¨Ù‘ÙØ­Ù’ Ø¨ÙØ­ÙŽÙ…Ù’Ø¯Ù Ø±ÙŽØ¨Ù‘ÙÙƒÙŽ ÙˆÙŽØ§Ø³Ù’ØªÙŽØºÙ’ÙÙØ±Ù’Ù‡Ù Ûš Ø¥ÙÙ†Ù‘ÙŽÙ‡Ù ÙƒÙŽØ§Ù†ÙŽ ØªÙŽÙˆÙ‘ÙŽØ§Ø¨Ù‹Ø§" }
  ],
  112: [
    { number: 1, text: "Ù‚ÙÙ„Ù’ Ù‡ÙÙˆÙŽ Ø§Ù„Ù„Ù‘ÙŽÙ‡Ù Ø£ÙŽØ­ÙŽØ¯ÙŒ" },
    { number: 2, text: "Ø§Ù„Ù„Ù‘ÙŽÙ‡Ù Ø§Ù„ØµÙ‘ÙŽÙ…ÙŽØ¯Ù" },
    { number: 3, text: "Ù„ÙŽÙ…Ù’ ÙŠÙŽÙ„ÙØ¯Ù’ ÙˆÙŽÙ„ÙŽÙ…Ù’ ÙŠÙÙˆÙ„ÙŽØ¯Ù’" },
    { number: 4, text: "ÙˆÙŽÙ„ÙŽÙ…Ù’ ÙŠÙŽÙƒÙÙ† Ù„Ù‘ÙŽÙ‡Ù ÙƒÙÙÙÙˆÙ‹Ø§ Ø£ÙŽØ­ÙŽØ¯ÙŒ" }
  ],
  113: [
    { number: 1, text: "Ù‚ÙÙ„Ù’ Ø£ÙŽØ¹ÙÙˆØ°Ù Ø¨ÙØ±ÙŽØ¨Ù‘Ù Ø§Ù„Ù’ÙÙŽÙ„ÙŽÙ‚Ù" },
    { number: 2, text: "Ù…ÙÙ† Ø´ÙŽØ±Ù‘Ù Ù…ÙŽØ§ Ø®ÙŽÙ„ÙŽÙ‚ÙŽ" },
    { number: 3, text: "ÙˆÙŽÙ…ÙÙ† Ø´ÙŽØ±Ù‘Ù ØºÙŽØ§Ø³ÙÙ‚Ù Ø¥ÙØ°ÙŽØ§ ÙˆÙŽÙ‚ÙŽØ¨ÙŽ" },
    { number: 4, text: "ÙˆÙŽÙ…ÙÙ† Ø´ÙŽØ±Ù‘Ù Ø§Ù„Ù†Ù‘ÙŽÙÙ‘ÙŽØ§Ø«ÙŽØ§ØªÙ ÙÙÙŠ Ø§Ù„Ù’Ø¹ÙÙ‚ÙŽØ¯Ù" },
    { number: 5, text: "ÙˆÙŽÙ…ÙÙ† Ø´ÙŽØ±Ù‘Ù Ø­ÙŽØ§Ø³ÙØ¯Ù Ø¥ÙØ°ÙŽØ§ Ø­ÙŽØ³ÙŽØ¯ÙŽ" }
  ],
  114: [
    { number: 1, text: "Ù‚ÙÙ„Ù’ Ø£ÙŽØ¹ÙÙˆØ°Ù Ø¨ÙØ±ÙŽØ¨Ù‘Ù Ø§Ù„Ù†Ù‘ÙŽØ§Ø³Ù" },
    { number: 2, text: "Ù…ÙŽÙ„ÙÙƒÙ Ø§Ù„Ù†Ù‘ÙŽØ§Ø³Ù" },
    { number: 3, text: "Ø¥ÙÙ„ÙŽÙ°Ù‡Ù Ø§Ù„Ù†Ù‘ÙŽØ§Ø³Ù" },
    { number: 4, text: "Ù…ÙÙ† Ø´ÙŽØ±Ù‘Ù Ø§Ù„Ù’ÙˆÙŽØ³Ù’ÙˆÙŽØ§Ø³Ù Ø§Ù„Ù’Ø®ÙŽÙ†Ù‘ÙŽØ§Ø³Ù" },
    { number: 5, text: "Ø§Ù„Ù‘ÙŽØ°ÙÙŠ ÙŠÙÙˆÙŽØ³Ù’ÙˆÙØ³Ù ÙÙÙŠ ØµÙØ¯ÙÙˆØ±Ù Ø§Ù„Ù†Ù‘ÙŽØ§Ø³Ù" },
    { number: 6, text: "Ù…ÙÙ†ÙŽ Ø§Ù„Ù’Ø¬ÙÙ†Ù‘ÙŽØ©Ù ÙˆÙŽØ§Ù„Ù†Ù‘ÙŽØ§Ø³Ù" }
  ]
};

export async function fetchFullSurahText(surahNumber) {
  if (VERIFIED_SURAHS_TEXT[surahNumber]) {
    return VERIFIED_SURAHS_TEXT[surahNumber];
  }
  try {
    const res = await fetch(`https://api.alquran.cloud/v1/surah/${surahNumber}`);
    if (res.ok) {
      const data = await res.json();
      return data.data.ayahs.map(a => ({
        number: a.numberInSurah,
        text: a.text
      }));
    }
  } catch (e) {
    console.warn("Could not fetch remote surah, using fallback", e);
  }
  return null;
}
// Authentic Morning and Evening Azkar dataset with counts, virtues, and categories

export const AZKAR_DATA = {
  morning: [
    {
      id: "m1",
      text: "Ø£ÙŽØµÙ’Ø¨ÙŽØ­Ù’Ù†ÙŽØ§ ÙˆÙŽØ£ÙŽØµÙ’Ø¨ÙŽØ­ÙŽ Ø§Ù„Ù’Ù…ÙÙ„Ù’ÙƒÙ Ù„ÙÙ„ÙŽÙ‘Ù‡ÙØŒ ÙˆÙŽØ§Ù„Ù’Ø­ÙŽÙ…Ù’Ø¯Ù Ù„ÙÙ„ÙŽÙ‘Ù‡ÙØŒ Ù„Ø§ÙŽ Ø¥ÙÙ„ÙŽÙ€Ù‡ÙŽ Ø¥ÙÙ„Ø§ÙŽÙ‘ Ø§Ù„Ù„Ù‡Ù ÙˆÙŽØ­Ù’Ø¯ÙŽÙ‡Ù Ù„Ø§ÙŽ Ø´ÙŽØ±ÙÙŠÙƒÙŽ Ù„ÙŽÙ‡ÙØŒ Ù„ÙŽÙ‡Ù Ø§Ù„Ù’Ù…ÙÙ„Ù’ÙƒÙ ÙˆÙŽÙ„ÙŽÙ‡Ù Ø§Ù„Ù’Ø­ÙŽÙ…Ù’Ø¯Ù ÙˆÙŽÙ‡ÙÙˆÙŽ Ø¹ÙŽÙ„ÙŽÙ‰ ÙƒÙÙ„ÙÙ‘ Ø´ÙŽÙŠÙ’Ø¡Ù Ù‚ÙŽØ¯ÙÙŠØ±ÙŒ.",
      count: 1,
      virtue: "Ù…Ù† Ù‚Ø§Ù„Ù‡Ø§ Ø­ÙŠÙ† ÙŠØµØ¨Ø­ Ø£ÙØ¹Ø·ÙŠ Ø®ÙŠØ± Ù…Ø§ ÙÙŠ Ù‡Ø°Ø§ Ø§Ù„ÙŠÙˆÙ… ÙˆØ®ÙŠØ± Ù…Ø§ Ø¨Ø¹Ø¯Ù‡."
    },
    {
      id: "m2",
      text: "Ø§Ù„Ù„ÙŽÙ‘Ù‡ÙÙ…ÙŽÙ‘ Ø£ÙŽÙ†Ù’ØªÙŽ Ø±ÙŽØ¨ÙÙ‘ÙŠ Ù„Ø§ÙŽ Ø¥ÙÙ„ÙŽÙ‡ÙŽ Ø¥ÙÙ„Ø§ÙŽÙ‘ Ø£ÙŽÙ†Ù’ØªÙŽØŒ Ø®ÙŽÙ„ÙŽÙ‚Ù’ØªÙŽÙ†ÙÙŠ ÙˆÙŽØ£ÙŽÙ†ÙŽØ§ Ø¹ÙŽØ¨Ù’Ø¯ÙÙƒÙŽØŒ ÙˆÙŽØ£ÙŽÙ†ÙŽØ§ Ø¹ÙŽÙ„ÙŽÙ‰ Ø¹ÙŽÙ‡Ù’Ø¯ÙÙƒÙŽ ÙˆÙŽÙˆÙŽØ¹Ù’Ø¯ÙÙƒÙŽ Ù…ÙŽØ§ Ø§Ø³Ù’ØªÙŽØ·ÙŽØ¹Ù’ØªÙØŒ Ø£ÙŽØ¹ÙÙˆØ°Ù Ø¨ÙÙƒÙŽ Ù…ÙÙ†Ù’ Ø´ÙŽØ±ÙÙ‘ Ù…ÙŽØ§ ØµÙŽÙ†ÙŽØ¹Ù’ØªÙØŒ Ø£ÙŽØ¨ÙÙˆØ¡Ù Ù„ÙŽÙƒÙŽ Ø¨ÙÙ†ÙØ¹Ù’Ù…ÙŽØªÙÙƒÙŽ Ø¹ÙŽÙ„ÙŽÙŠÙŽÙ‘ØŒ ÙˆÙŽØ£ÙŽØ¨ÙÙˆØ¡Ù Ø¨ÙØ°ÙŽÙ†Ù’Ø¨ÙÙŠ ÙÙŽØ§ØºÙ’ÙÙØ±Ù’ Ù„ÙÙŠ ÙÙŽØ¥ÙÙ†ÙŽÙ‘Ù‡Ù Ù„Ø§ÙŽ ÙŠÙŽØºÙ’ÙÙØ±Ù Ø§Ù„Ø°ÙÙ‘Ù†ÙÙˆØ¨ÙŽ Ø¥ÙÙ„Ø§ÙŽÙ‘ Ø£ÙŽÙ†Ù’ØªÙŽ.",
      count: 1,
      virtue: "Ø³ÙŠØ¯ Ø§Ù„Ø§Ø³ØªØºÙØ§Ø±ØŒ Ù…Ù† Ù‚Ø§Ù„Ù‡ Ù…ÙˆÙ‚Ù†Ø§Ù‹ Ø¨Ù‡ ÙˆÙ…Ø§Øª Ø¯Ø®Ù„ Ø§Ù„Ø¬Ù†Ø©."
    },
    {
      id: "m3",
      text: "Ø¨ÙØ³Ù’Ù…Ù Ø§Ù„Ù„ÙŽÙ‘Ù‡Ù Ø§Ù„ÙŽÙ‘Ø°ÙÙŠ Ù„ÙŽØ§ ÙŠÙŽØ¶ÙØ±ÙÙ‘ Ù…ÙŽØ¹ÙŽ Ø§Ø³Ù’Ù…ÙÙ‡Ù Ø´ÙŽÙŠÙ’Ø¡ÙŒ ÙÙÙŠ Ø§Ù„Ù’Ø£ÙŽØ±Ù’Ø¶Ù ÙˆÙŽÙ„ÙŽØ§ ÙÙÙŠ Ø§Ù„Ø³ÙŽÙ‘Ù…ÙŽØ§Ø¡Ù ÙˆÙŽÙ‡ÙÙˆÙŽ Ø§Ù„Ø³ÙŽÙ‘Ù…ÙÙŠØ¹Ù Ø§Ù„Ù’Ø¹ÙŽÙ„ÙÙŠÙ…Ù.",
      count: 3,
      virtue: "Ù„Ù… ÙŠØ¶Ø±Ù‡ Ù…Ù† Ø§Ù„Ù„Ù‡ Ø´ÙŠØ¡ ÙˆØ¹ÙˆÙÙŠ Ù…Ù† Ø§Ù„ÙØ¬Ø§Ø¡Ø©."
    },
    {
      id: "m4",
      text: "Ø±ÙŽØ¶ÙÙŠØªÙ Ø¨ÙØ§Ù„Ù„ÙŽÙ‘Ù‡Ù Ø±ÙŽØ¨Ù‹Ù‘Ø§ØŒ ÙˆÙŽØ¨ÙØ§Ù„Ø¥ÙØ³Ù’Ù„Ø§ÙŽÙ…Ù Ø¯ÙÙŠÙ†Ù‹Ø§ØŒ ÙˆÙŽØ¨ÙÙ…ÙØ­ÙŽÙ…ÙŽÙ‘Ø¯Ù ØµÙ„Ù‰ Ø§Ù„Ù„Ù‡ Ø¹Ù„ÙŠÙ‡ ÙˆØ³Ù„Ù… Ù†ÙŽØ¨ÙÙŠÙ‹Ù‘Ø§.",
      count: 3,
      virtue: "ÙƒØ§Ù† Ø­Ù‚Ø§Ù‹ Ø¹Ù„Ù‰ Ø§Ù„Ù„Ù‡ Ø£Ù† ÙŠØ±Ø¶ÙŠÙ‡ ÙŠÙˆÙ… Ø§Ù„Ù‚ÙŠØ§Ù…Ø©."
    },
    {
      id: "m5",
      text: "ÙŠÙŽØ§ Ø­ÙŽÙŠÙÙ‘ ÙŠÙŽØ§ Ù‚ÙŽÙŠÙÙ‘ÙˆÙ…Ù Ø¨ÙØ±ÙŽØ­Ù’Ù…ÙŽØªÙÙƒÙŽ Ø£ÙŽØ³Ù’ØªÙŽØºÙÙŠØ«ÙØŒ Ø£ÙŽØµÙ’Ù„ÙØ­Ù’ Ù„ÙÙŠ Ø´ÙŽØ£Ù’Ù†ÙÙŠ ÙƒÙÙ„ÙŽÙ‘Ù‡ÙØŒ ÙˆÙŽÙ„Ø§ÙŽ ØªÙŽÙƒÙÙ„Ù’Ù†ÙÙŠ Ø¥ÙÙ„ÙŽÙ‰ Ù†ÙŽÙÙ’Ø³ÙÙŠ Ø·ÙŽØ±Ù’ÙÙŽØ©ÙŽ Ø¹ÙŽÙŠÙ’Ù†Ù.",
      count: 1,
      virtue: "ØµÙ„Ø§Ø­ Ø§Ù„Ø­Ø§Ù„ ÙˆØ¯ÙØ¹ Ø§Ù„Ø´Ø±ÙˆØ± ÙˆØªÙÙˆÙŠØ¶ Ø§Ù„Ø£Ù…Ø± Ù„Ù„Ù‡."
    },
    {
      id: "m6",
      text: "Ø§Ù„Ù„ÙŽÙ‘Ù‡ÙÙ…ÙŽÙ‘ Ø¨ÙÙƒÙŽ Ø£ÙŽØµÙ’Ø¨ÙŽØ­Ù’Ù†ÙŽØ§ØŒ ÙˆÙŽØ¨ÙÙƒÙŽ Ø£ÙŽÙ…Ù’Ø³ÙŽÙŠÙ’Ù†ÙŽØ§ØŒ ÙˆÙŽØ¨ÙÙƒÙŽ Ù†ÙŽØ­Ù’ÙŠÙŽØ§ØŒ ÙˆÙŽØ¨ÙÙƒÙŽ Ù†ÙŽÙ…ÙÙˆØªÙØŒ ÙˆÙŽØ¥ÙÙ„ÙŽÙŠÙ’ÙƒÙŽ Ø§Ù„Ù†ÙÙ‘Ø´ÙÙˆØ±Ù.",
      count: 1,
      virtue: "Ø´ÙƒØ± Ù†Ø¹Ù…Ø© Ø§Ù„ØµØ¨Ø§Ø­ ÙˆØ§Ù„Ø­ÙŠØ§Ø©."
    },
    {
      id: "m7",
      text: "Ø³ÙØ¨Ù’Ø­ÙŽØ§Ù†ÙŽ Ø§Ù„Ù„ÙŽÙ‘Ù‡Ù ÙˆÙŽØ¨ÙØ­ÙŽÙ…Ù’Ø¯ÙÙ‡ÙØŒ Ø¹ÙŽØ¯ÙŽØ¯ÙŽ Ø®ÙŽÙ„Ù’Ù‚ÙÙ‡ÙØŒ ÙˆÙŽØ±ÙØ¶ÙŽØ§ Ù†ÙŽÙÙ’Ø³ÙÙ‡ÙØŒ ÙˆÙŽØ²ÙÙ†ÙŽØ©ÙŽ Ø¹ÙŽØ±Ù’Ø´ÙÙ‡ÙØŒ ÙˆÙŽÙ…ÙØ¯ÙŽØ§Ø¯ÙŽ ÙƒÙŽÙ„ÙÙ…ÙŽØ§ØªÙÙ‡Ù.",
      count: 3,
      virtue: "ØªØ¹Ø¯Ù„ Ø³Ø§Ø¹Ø§Øª Ø·ÙˆÙŠÙ„Ø© Ù…Ù† Ø§Ù„Ø°ÙƒØ± ÙˆØ§Ù„ØªØ³Ø¨ÙŠØ­."
    },
    {
      id: "m8",
      text: "Ø­ÙŽØ³Ù’Ø¨ÙÙŠÙŽ Ø§Ù„Ù„ÙŽÙ‘Ù‡Ù Ù„Ø§ÙŽ Ø¥ÙÙ„ÙŽÙ€Ù‡ÙŽ Ø¥ÙÙ„Ø§ÙŽÙ‘ Ù‡ÙÙˆÙŽ Ø¹ÙŽÙ„ÙŽÙŠÙ’Ù‡Ù ØªÙŽÙˆÙŽÙƒÙŽÙ‘Ù„Ù’ØªÙ ÙˆÙŽÙ‡ÙÙˆÙŽ Ø±ÙŽØ¨ÙÙ‘ Ø§Ù„Ù’Ø¹ÙŽØ±Ù’Ø´Ù Ø§Ù„Ù’Ø¹ÙŽØ¸ÙÙŠÙ…Ù.",
      count: 7,
      virtue: "ÙƒÙØ§Ù‡ Ø§Ù„Ù„Ù‡ Ù…Ø§ Ø£Ù‡Ù…Ù‡ Ù…Ù† Ø£Ù…Ø± Ø¯Ù†ÙŠØ§Ù‡ ÙˆØ¢Ø®Ø±ØªÙ‡."
    }
  ],
  evening: [
    {
      id: "e1",
      text: "Ø£ÙŽÙ…Ù’Ø³ÙŽÙŠÙ’Ù†ÙŽØ§ ÙˆÙŽØ£ÙŽÙ…Ù’Ø³ÙŽÙ‰ Ø§Ù„Ù’Ù…ÙÙ„Ù’ÙƒÙ Ù„ÙÙ„ÙŽÙ‘Ù‡ÙØŒ ÙˆÙŽØ§Ù„Ù’Ø­ÙŽÙ…Ù’Ø¯Ù Ù„ÙÙ„ÙŽÙ‘Ù‡ÙØŒ Ù„Ø§ÙŽ Ø¥ÙÙ„ÙŽÙ€Ù‡ÙŽ Ø¥ÙÙ„Ø§ÙŽÙ‘ Ø§Ù„Ù„Ù‡Ù ÙˆÙŽØ­Ù’Ø¯ÙŽÙ‡Ù Ù„Ø§ÙŽ Ø´ÙŽØ±ÙÙŠÙƒÙŽ Ù„ÙŽÙ‡ÙØŒ Ù„ÙŽÙ‡Ù Ø§Ù„Ù’Ù…ÙÙ„Ù’ÙƒÙ ÙˆÙŽÙ„ÙŽÙ‡Ù Ø§Ù„Ù’Ø­ÙŽÙ…Ù’Ø¯Ù ÙˆÙŽÙ‡ÙÙˆÙŽ Ø¹ÙŽÙ„ÙŽÙ‰ ÙƒÙÙ„ÙÙ‘ Ø´ÙŽÙŠÙ’Ø¡Ù Ù‚ÙŽØ¯ÙÙŠØ±ÙŒ.",
      count: 1,
      virtue: "Ø­ÙØ¸ Ø§Ù„Ø¹Ø¨Ø¯ ÙÙŠ Ù„ÙŠÙ„ØªÙ‡ ÙˆÙ†ÙŠÙ„ Ø§Ù„Ø£Ø¬Ø± Ø§Ù„Ø¹Ø¸ÙŠÙ…."
    },
    {
      id: "e2",
      text: "Ø§Ù„Ù„ÙŽÙ‘Ù‡ÙÙ…ÙŽÙ‘ Ø£ÙŽÙ†Ù’ØªÙŽ Ø±ÙŽØ¨ÙÙ‘ÙŠ Ù„Ø§ÙŽ Ø¥ÙÙ„ÙŽÙ‡ÙŽ Ø¥ÙÙ„Ø§ÙŽÙ‘ Ø£ÙŽÙ†Ù’ØªÙŽØŒ Ø®ÙŽÙ„ÙŽÙ‚Ù’ØªÙŽÙ†ÙÙŠ ÙˆÙŽØ£ÙŽÙ†ÙŽØ§ Ø¹ÙŽØ¨Ù’Ø¯ÙÙƒÙŽØŒ ÙˆÙŽØ£ÙŽÙ†ÙŽØ§ Ø¹ÙŽÙ„ÙŽÙ‰ Ø¹ÙŽÙ‡Ù’Ø¯ÙÙƒÙŽ ÙˆÙŽÙˆÙŽØ¹Ù’Ø¯ÙÙƒÙŽ Ù…ÙŽØ§ Ø§Ø³Ù’ØªÙŽØ·ÙŽØ¹Ù’ØªÙØŒ Ø£ÙŽØ¹ÙÙˆØ°Ù Ø¨ÙÙƒÙŽ Ù…ÙÙ†Ù’ Ø´ÙŽØ±ÙÙ‘ Ù…ÙŽØ§ ØµÙŽÙ†ÙŽØ¹Ù’ØªÙØŒ Ø£ÙŽØ¨ÙÙˆØ¡Ù Ù„ÙŽÙƒÙŽ Ø¨ÙÙ†ÙØ¹Ù’Ù…ÙŽØªÙÙƒÙŽ Ø¹ÙŽÙ„ÙŽÙŠÙŽÙ‘ØŒ ÙˆÙŽØ£ÙŽØ¨ÙÙˆØ¡Ù Ø¨ÙØ°ÙŽÙ†Ù’Ø¨ÙÙŠ ÙÙŽØ§ØºÙ’ÙÙØ±Ù’ Ù„ÙÙŠ ÙÙŽØ¥ÙÙ†ÙŽÙ‘Ù‡Ù Ù„Ø§ÙŽ ÙŠÙŽØºÙ’ÙÙØ±Ù Ø§Ù„Ø°ÙÙ‘Ù†ÙÙˆØ¨ÙŽ Ø¥ÙÙ„Ø§ÙŽÙ‘ Ø£ÙŽÙ†Ù’ØªÙŽ.",
      count: 1,
      virtue: "Ø³ÙŠØ¯ Ø§Ù„Ø§Ø³ØªØºÙØ§Ø± ÙÙŠ Ø§Ù„Ù…Ø³Ø§Ø¡."
    },
    {
      id: "e3",
      text: "Ø£ÙŽØ¹ÙÙˆØ°Ù Ø¨ÙÙƒÙŽÙ„ÙÙ…ÙŽØ§ØªÙ Ø§Ù„Ù„ÙŽÙ‘Ù‡Ù Ø§Ù„ØªÙŽÙ‘Ø§Ù…ÙŽÙ‘Ø§ØªÙ Ù…ÙÙ†Ù’ Ø´ÙŽØ±ÙÙ‘ Ù…ÙŽØ§ Ø®ÙŽÙ„ÙŽÙ‚ÙŽ.",
      count: 3,
      virtue: "Ø­Ù…Ø§ÙŠØ© ØªØ§Ù…Ø© Ù…Ù† ÙƒÙ„ Ø°ÙŠ Ø´Ø± ÙˆÙ„ÙŽÙ… ÙŠØ¶Ø±Ù‡ Ø´ÙŠØ¡ Ø­ØªÙ‰ ÙŠØµØ¨Ø­."
    },
    {
      id: "e4",
      text: "Ø¨ÙØ³Ù’Ù…Ù Ø§Ù„Ù„ÙŽÙ‘Ù‡Ù Ø§Ù„ÙŽÙ‘Ø°ÙÙŠ Ù„ÙŽØ§ ÙŠÙŽØ¶ÙØ±ÙÙ‘ Ù…ÙŽØ¹ÙŽ Ø§Ø³Ù’Ù…ÙÙ‡Ù Ø´ÙŽÙŠÙ’Ø¡ÙŒ ÙÙÙŠ Ø§Ù„Ù’Ø£ÙŽØ±Ù’Ø¶Ù ÙˆÙŽÙ„ÙŽØ§ ÙÙÙŠ Ø§Ù„Ø³ÙŽÙ‘Ù…ÙŽØ§Ø¡Ù ÙˆÙŽÙ‡ÙÙˆÙŽ Ø§Ù„Ø³ÙŽÙ‘Ù…ÙÙŠØ¹Ù Ø§Ù„Ù’Ø¹ÙŽÙ„ÙÙŠÙ…Ù.",
      count: 3,
      virtue: "Ø£Ù…Ù† ÙˆØ³Ù„Ø§Ù…Ø© ÙˆØ­ÙØ¸ Ø­ØªÙ‰ ÙŠØµØ¨Ø­."
    },
    {
      id: "e5",
      text: "Ø±ÙŽØ¶ÙÙŠØªÙ Ø¨ÙØ§Ù„Ù„ÙŽÙ‘Ù‡Ù Ø±ÙŽØ¨Ù‹Ù‘Ø§ØŒ ÙˆÙŽØ¨ÙØ§Ù„Ø¥ÙØ³Ù’Ù„Ø§ÙŽÙ…Ù Ø¯ÙÙŠÙ†Ù‹Ø§ØŒ ÙˆÙŽØ¨ÙÙ…ÙØ­ÙŽÙ…ÙŽÙ‘Ø¯Ù ØµÙ„Ù‰ Ø§Ù„Ù„Ù‡ Ø¹Ù„ÙŠÙ‡ ÙˆØ³Ù„Ù… Ù†ÙŽØ¨ÙÙŠÙ‹Ù‘Ø§.",
      count: 3,
      virtue: "Ø±Ø¶Ø§ Ø§Ù„Ù„Ù‡ ØªØ¹Ø§Ù„Ù‰ ÙŠÙˆÙ… Ø§Ù„Ù‚ÙŠØ§Ù…Ø©."
    },
    {
      id: "e6",
      text: "Ø§Ù„Ù„ÙŽÙ‘Ù‡ÙÙ…ÙŽÙ‘ Ø¨ÙÙƒÙŽ Ø£ÙŽÙ…Ù’Ø³ÙŽÙŠÙ’Ù†ÙŽØ§ØŒ ÙˆÙŽØ¨ÙÙƒÙŽ Ø£ÙŽØµÙ’Ø¨ÙŽØ­Ù’Ù†ÙŽØ§ØŒ ÙˆÙŽØ¨ÙÙƒÙŽ Ù†ÙŽØ­Ù’ÙŠÙŽØ§ØŒ ÙˆÙŽØ¨ÙÙƒÙŽ Ù†ÙŽÙ…ÙÙˆØªÙØŒ ÙˆÙŽØ¥ÙÙ„ÙŽÙŠÙ’ÙƒÙŽ Ø§Ù„Ù’Ù…ÙŽØµÙÙŠØ±Ù.",
      count: 1,
      virtue: "Ø´ÙƒØ± Ù†Ø¹Ù…Ø© Ø§Ù„Ù…Ø³Ø§Ø¡ ÙˆØ§Ù„Ø§Ø³ØªØ³Ù„Ø§Ù… Ù„Ù„Ù‡ ØªØ¹Ø§Ù„Ù‰."
    },
    {
      id: "e7",
      text: "Ø­ÙŽØ³Ù’Ø¨ÙÙŠÙŽ Ø§Ù„Ù„ÙŽÙ‘Ù‡Ù Ù„Ø§ÙŽ Ø¥ÙÙ„ÙŽÙ€Ù‡ÙŽ Ø¥ÙÙ„Ø§ÙŽÙ‘ Ù‡ÙÙˆÙŽ Ø¹ÙŽÙ„ÙŽÙŠÙ’Ù‡Ù ØªÙŽÙˆÙŽÙƒÙŽÙ‘Ù„Ù’ØªÙ ÙˆÙŽÙ‡ÙÙˆÙŽ Ø±ÙŽØ¨ÙÙ‘ Ø§Ù„Ù’Ø¹ÙŽØ±Ù’Ø´Ù Ø§Ù„Ù’Ø¹ÙŽØ¸ÙÙŠÙ…Ù.",
      count: 7,
      virtue: "ÙƒÙØ§ÙŠØ© Ø§Ù„Ù„Ù‡ Ù…Ù† Ø§Ù„Ù‡Ù…ÙˆÙ… ÙˆØ§Ù„Ø´Ø±ÙˆØ±."
    }
  ]
};
// Comprehensive Arabic Trivia and Questions Bank for the 2-Player Quiz Game

export const QUESTIONS_BANK = [
  {
    id: 1,
    category: "Ø§Ù„Ø¹Ù„ÙˆÙ…",
    question: "Ù…Ø§ Ù‡Ùˆ Ø§Ù„Ø¹Ù†ØµØ± Ø§Ù„Ø£ÙƒØ«Ø± ÙˆÙØ±Ø© ÙÙŠ Ø§Ù„ØºÙ„Ø§Ù Ø§Ù„Ø¬ÙˆÙŠ Ù„Ù„Ø£Ø±Ø¶ØŸ",
    options: ["Ø§Ù„Ø£ÙƒØ³Ø¬ÙŠÙ†", "Ø§Ù„Ù†ÙŠØªØ±ÙˆØ¬ÙŠÙ†", "Ø«Ø§Ù†ÙŠ Ø£ÙƒØ³ÙŠØ¯ Ø§Ù„ÙƒØ±Ø¨ÙˆÙ†", "Ø§Ù„Ù‡ÙŠØ¯Ø±ÙˆØ¬ÙŠÙ†"],
    correct: 1,
    points: 15
  },
  {
    id: 2,
    category: "Ø§Ù„Ø¬ØºØ±Ø§ÙÙŠØ§",
    question: "Ù…Ø§ Ù‡ÙŠ Ø£Ø·ÙˆÙ„ Ù‚Ù…Ø© Ø¬Ø¨Ù„ÙŠØ© ÙÙŠ Ø§Ù„Ø¹Ø§Ù„Ù… Ø§Ù„Ø¹Ø±Ø¨ÙŠØŸ",
    options: ["Ø¬Ø¨Ù„ ØªÙˆØ¨Ù‚Ø§Ù„", "Ø¬Ø¨Ù„ Ø§Ù„Ø´ÙŠØ®", "Ø¬Ø¨Ù„ ÙƒØ§ØªØ±ÙŠÙ†", "Ø¬Ø¨Ù„ Ø´Ù…Ø³"],
    correct: 0,
    points: 15
  },
  {
    id: 3,
    category: "Ø§Ù„ØªØ§Ø±ÙŠØ®",
    question: "ÙÙŠ Ø£ÙŠ Ø¹Ø§Ù… ØªÙ… Ø¨Ù†Ø§Ø¡ Ø¬Ø§Ù…Ø¹ Ø§Ù„Ø£Ø²Ù‡Ø± Ø§Ù„Ø´Ø±ÙŠÙ ÙÙŠ Ø§Ù„Ù‚Ø§Ù‡Ø±Ø©ØŸ",
    options: ["970 Ù…", "850 Ù…", "1020 Ù…", "1187 Ù…"],
    correct: 0,
    points: 20
  },
  {
    id: 4,
    category: "Ø§Ù„ØªÙƒÙ†ÙˆÙ„ÙˆØ¬ÙŠØ§",
    question: "Ù…Ø§ Ù‡Ùˆ Ø§Ù„Ø¨Ø±ÙˆØªÙˆÙƒÙˆÙ„ Ø§Ù„Ù…Ø³ØªØ®Ø¯Ù… Ù„ØªØµÙØ­ ØµÙØ­Ø§Øª Ø§Ù„ÙˆÙŠØ¨ Ø§Ù„Ø¢Ù…Ù†Ø© ÙˆØ§Ù„Ù…Ø´ÙØ±Ø©ØŸ",
    options: ["FTP", "SMTP", "HTTPS", "SSH"],
    correct: 2,
    points: 10
  },
  {
    id: 5,
    category: "Ø¹Ø§Ù…Ø© ÙˆÙ…Ø±Ø­Ø©",
    question: "Ù…Ø§ Ù‡Ùˆ Ø§Ù„Ø­ÙŠÙˆØ§Ù† Ø§Ù„Ø°ÙŠ Ù„Ø§ ÙŠØ³ØªØ·ÙŠØ¹ Ø§Ù„Ù‚ÙØ² Ø¥Ø·Ù„Ø§Ù‚Ø§Ù‹ØŸ",
    options: ["Ø§Ù„ÙÙŠÙ„", "Ø§Ù„ÙƒÙ†ØºØ±", "Ø§Ù„Ø¶ÙØ¯Ø¹", "Ø§Ù„Ø£Ø±Ù†Ø¨"],
    correct: 0,
    points: 10
  },
  {
    id: 6,
    category: "Ø§Ù„Ø±ÙŠØ§Ø¶Ø©",
    question: "ÙƒÙ… Ø¹Ø¯Ø¯ Ù„Ø§Ø¹Ø¨ÙŠ ÙØ±ÙŠÙ‚ ÙƒØ±Ø© Ø§Ù„Ù…Ø§Ø¡ Ø¯Ø§Ø®Ù„ Ø­ÙˆØ¶ Ø§Ù„Ø³Ø¨Ø§Ø­Ø© ÙÙŠ Ø§Ù„Ù…Ø¨Ø§Ø±Ø§Ø©ØŸ",
    options: ["5 Ù„Ø§Ø¹Ø¨ÙŠÙ†", "6 Ù„Ø§Ø¹Ø¨ÙŠÙ†", "7 Ù„Ø§Ø¹Ø¨ÙŠÙ†", "8 Ù„Ø§Ø¹Ø¨ÙŠÙ†"],
    correct: 2,
    points: 15
  },
  {
    id: 7,
    category: "Ø§Ù„Ø¹Ù„ÙˆÙ…",
    question: "Ø£ÙŠ Ù…Ù† ÙƒÙˆØ§ÙƒØ¨ Ø§Ù„Ù…Ø¬Ù…ÙˆØ¹Ø© Ø§Ù„Ø´Ù…Ø³ÙŠØ© ÙŠÙØ¹Ø±Ù Ø¨Ù„Ù‚Ø¨ 'Ø§Ù„ÙƒÙˆÙƒØ¨ Ø§Ù„Ø£Ø­Ù…Ø±'ØŸ",
    options: ["Ø§Ù„Ø²Ù‡Ø±Ø©", "Ø§Ù„Ù…Ø±ÙŠØ®", "Ø§Ù„Ù…Ø´ØªØ±ÙŠ", "Ø¹Ø·Ø§Ø±Ø¯"],
    correct: 1,
    points: 10
  },
  {
    id: 8,
    category: "Ø°ÙƒØ§Ø¡ ÙˆÙ…Ù†Ø·Ù‚",
    question: "Ø´ÙŠØ¡ ÙƒÙ„Ù…Ø§ Ø²Ø§Ø¯ Ù†Ù‚ØµØŒ ÙÙ…Ø§ Ù‡ÙˆØŸ",
    options: ["Ø§Ù„Ù…Ø§Ù„", "Ø§Ù„Ø¹Ù…Ø±", "Ø§Ù„Ø­ÙØ±Ø©", "Ø§Ù„Ø¹Ù„Ù…"],
    correct: 1,
    points: 10
  },
  {
    id: 9,
    category: "Ø§Ù„ØªØ§Ø±ÙŠØ®",
    question: "Ù…Ù† Ù‡Ùˆ Ø§Ù„Ù‚Ø§Ø¦Ø¯ Ø§Ù„Ù…Ø³Ù„Ù… Ø§Ù„Ø°ÙŠ ÙØªØ­ Ø¨Ù„Ø§Ø¯ Ø§Ù„Ø£Ù†Ø¯Ù„Ø³ØŸ",
    options: ["Ø·Ø§Ø±Ù‚ Ø¨Ù† Ø²ÙŠØ§Ø¯", "Ø®Ø§Ù„Ø¯ Ø¨Ù† Ø§Ù„ÙˆÙ„ÙŠØ¯", "ØµÙ„Ø§Ø­ Ø§Ù„Ø¯ÙŠÙ† Ø§Ù„Ø£ÙŠÙˆØ¨ÙŠ", "Ø¹Ù…Ø±Ùˆ Ø¨Ù† Ø§Ù„Ø¹Ø§Øµ"],
    correct: 0,
    points: 15
  },
  {
    id: 10,
    category: "Ø§Ù„Ø¬ØºØ±Ø§ÙÙŠØ§",
    question: "Ù…Ø§ Ù‡ÙŠ Ø¹Ø§ØµÙ…Ø© Ø¯ÙˆÙ„Ø© Ø§Ù„Ø¨Ø±Ø§Ø²ÙŠÙ„ Ø§Ù„Ø­Ø§Ù„ÙŠØ©ØŸ",
    options: ["Ø±ÙŠÙˆ Ø¯ÙŠ Ø¬Ø§Ù†ÙŠØ±Ùˆ", "Ø³Ø§Ùˆ Ø¨Ø§ÙˆÙ„Ùˆ", "Ø¨Ø±Ø§Ø²ÙŠÙ„ÙŠØ§", "Ø³Ù„ÙØ§Ø¯ÙˆØ±"],
    correct: 2,
    points: 15
  },
  {
    id: 11,
    category: "Ø§Ù„Ø¹Ù„ÙˆÙ…",
    question: "Ù…Ø§ Ù‡ÙŠ Ø§Ù„Ù…Ø§Ø¯Ø© Ø§Ù„ØªÙŠ ØªØ¹Ø·ÙŠ Ø£ÙˆØ±Ø§Ù‚ Ø§Ù„Ù†Ø¨Ø§ØªØ§Øª Ù„ÙˆÙ†Ù‡Ø§ Ø§Ù„Ø£Ø®Ø¶Ø± ÙˆØªØ³Ø§Ø¹Ø¯ ÙÙŠ Ø§Ù„Ø¨Ù†Ø§Ø¡ Ø§Ù„Ø¶ÙˆØ¦ÙŠØŸ",
    options: ["Ø§Ù„Ù…ÙŠÙ„Ø§Ù†ÙŠÙ†", "Ø§Ù„ÙƒÙ„ÙˆØ±ÙˆÙÙŠÙ„", "Ø§Ù„ÙƒÙŠØ±Ø§ØªÙŠÙ†", "Ø§Ù„Ù‡ÙŠÙ…ÙˆØ¬Ù„ÙˆØ¨ÙŠÙ†"],
    correct: 1,
    points: 15
  },
  {
    id: 12,
    category: "ØªØ³Ù„ÙŠØ©",
    question: "Ù…Ø§ Ù‡Ùˆ Ø§Ù„ÙƒØ§Ø¦Ù† Ø§Ù„Ø¨Ø­Ø±ÙŠ Ø§Ù„Ø°ÙŠ ÙŠÙ…ØªÙ„Ùƒ Ø«Ù„Ø§Ø«Ø© Ù‚Ù„ÙˆØ¨ ÙˆØ¯Ù…Ø§Ù‹ Ø£Ø²Ø±Ù‚ Ø§Ù„Ù„ÙˆÙ†ØŸ",
    options: ["Ø§Ù„Ø­ÙˆØª Ø§Ù„Ø£Ø²Ø±Ù‚", "Ø§Ù„Ø£Ø®Ø·Ø¨ÙˆØ·", "Ø§Ù„Ù‚Ø±Ø´", "Ù†Ø¬Ù… Ø§Ù„Ø¨Ø­Ø±"],
    correct: 1,
    points: 20
  }
];
// Authentic Prayer Times Calculator (Islamic Astronomical formulas - Egyptian General Authority of Survey)

export class PrayerTimesCalculator {
  static getTimes(date = new Date(), lat = 30.0444, lng = 31.2357, timezone = 2) {
    // Default coordinates: Cairo, Egypt (can be updated via Geolocation)
    const d = date.getDate();
    const m = date.getMonth() + 1;
    const y = date.getFullYear();

    // Astronomical calculation
    const julianDate = this.toJulian(y, m, d) - lng / (15 * 24);
    const sunPos = this.sunPosition(julianDate);

    // Calculation angles: Fajr 19.5, Isha 17.5 (Egyptian General Survey)
    const fajrAngle = 19.5;
    const ishaAngle = 17.5;

    const noon = this.midDay(julianDate, sunPos);
    const fajr = noon - this.sunAngleTime(fajrAngle, lat, sunPos.declination);
    const sunrise = noon - this.sunAngleTime(0.833, lat, sunPos.declination);
    const asr = noon + this.asrTime(1, lat, sunPos.declination); // Shafi/Standard
    const maghrib = noon + this.sunAngleTime(0.833, lat, sunPos.declination);
    const isha = noon + this.sunAngleTime(ishaAngle, lat, sunPos.declination);

    return {
      fajr: this.formatTime(fajr + timezone - lng/15),
      sunrise: this.formatTime(sunrise + timezone - lng/15),
      dhuhr: this.formatTime(noon + timezone - lng/15),
      asr: this.formatTime(asr + timezone - lng/15),
      maghrib: this.formatTime(maghrib + timezone - lng/15),
      isha: this.formatTime(isha + timezone - lng/15)
    };
  }

  static toJulian(year, month, day) {
    if (month <= 2) {
      year -= 1;
      month += 12;
    }
    const a = Math.floor(year / 100);
    const b = 2 - a + Math.floor(a / 4);
    return Math.floor(365.25 * (year + 4716)) + Math.floor(30.6001 * (month + 1)) + day + b - 1524.5;
  }

  static sunPosition(jd) {
    const d = jd - 2451545.0;
    const g = this.fixAngle(357.529 + 0.98560028 * d);
    const q = this.fixAngle(280.459 + 0.98564736 * d);
    const l = this.fixAngle(q + 1.915 * Math.sin(this.dtr(g)) + 0.020 * Math.sin(this.dtr(2 * g)));
    const e = 23.439 - 0.00000036 * d;
    const ra = this.rtd(Math.atan2(Math.cos(this.dtr(e)) * Math.sin(this.dtr(l)), Math.cos(this.dtr(l)))) / 15;
    const declination = this.rtd(Math.asin(Math.sin(this.dtr(e)) * Math.sin(this.dtr(l))));
    return { declination, ra };
  }

  static midDay(jd, sunPos) {
    return 12 - sunPos.ra + (jd - Math.floor(jd) - 0.5) * 24;
  }

  static sunAngleTime(angle, lat, dec) {
    const cosH = (-Math.sin(this.dtr(angle)) - Math.sin(this.dtr(lat)) * Math.sin(this.dtr(dec))) /
                 (Math.cos(this.dtr(lat)) * Math.cos(this.dtr(dec)));
    if (cosH > 1 || cosH < -1) return 0;
    return this.rtd(Math.acos(cosH)) / 15;
  }

  static asrTime(factor, lat, dec) {
    const angle = -this.rtd(Math.atan(1 / (factor + Math.tan(this.dtr(Math.abs(lat - dec))))));
    return this.sunAngleTime(angle, lat, dec);
  }

  static dtr(d) { return (d * Math.PI) / 180.0; }
  static rtd(r) { return (r * 180.0) / Math.PI; }
  static fixAngle(a) { a = a - 360.0 * Math.floor(a / 360.0); return a < 0 ? a + 360.0 : a; }

  static formatTime(h) {
    h = h - 24 * Math.floor(h / 24);
    let hours = Math.floor(h);
    let mins = Math.floor((h - hours) * 60);
    const ampm = hours >= 12 ? 'Ù…' : 'Øµ';
    let h12 = hours % 12 || 12;
    return `${h12.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')} ${ampm}`;
  }
}
// Heartbeat Counter Module: Calculates exact elapsed time since chat creation
// Never resets on refresh, logout, or device switch.

export class HeartCounter {
  constructor(createdAtTimestamp, containerElement, onClickDetail) {
    this.createdAt = createdAtTimestamp;
    this.container = containerElement;
    this.onClickDetail = onClickDetail;
    this.timer = null;
    this.init();
  }

  init() {
    this.render();
    this.timer = setInterval(() => this.update(), 1000);
  }

  destroy() {
    if (this.timer) clearInterval(this.timer);
  }

  calculate() {
    const now = Date.now();
    const diff = Math.max(0, now - this.createdAt);

    const seconds = Math.floor((diff / 1000) % 60);
    const minutes = Math.floor((diff / (1000 * 60)) % 60);
    const hours = Math.floor((diff / (1000 * 60 * 60)) % 24);
    const totalDays = Math.floor(diff / (1000 * 60 * 60 * 24));
    const months = Math.floor(totalDays / 30);
    const daysInMonth = totalDays % 30;

    return { totalDays, months, daysInMonth, hours, minutes, seconds };
  }

  render() {
    if (!this.container) return;
    const time = this.calculate();

    this.container.innerHTML = `
      <div class="heart-counter-card glass-panel" style="
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 14px 22px;
        background: linear-gradient(135deg, rgba(254, 226, 226, 0.7) 0%, rgba(253, 242, 248, 0.7) 100%);
        border: 1px solid rgba(244, 63, 94, 0.3);
        cursor: pointer;
        margin-bottom: 16px;
      ">
        <div style="display: flex; align-items: center; gap: 14px;">
          <div class="heart-pulse" style="font-size: 32px; filter: drop-shadow(0 4px 10px rgba(244,63,94,0.4));">
            â¤ï¸
          </div>
          <div>
            <div style="font-size: 13px; color: #9f1239; font-weight: 600;">
              Ù…Ø¯Ø© ØªÙˆØ§ØµÙ„Ù†Ø§ Ø¨Ø§Ù„Ø­Ø¨ ÙˆØ§Ù„Ø£ÙŠØ§Ù…
            </div>
            <div style="font-size: 20px; font-weight: 800; color: #881337;">
              <span id="heart-days-num">${time.totalDays}</span> ÙŠÙˆÙ… Ù…ÙƒØªÙ…Ù„
            </div>
          </div>
        </div>
        <div style="display: flex; align-items: center; gap: 8px;">
          <span style="font-size: 12px; color: #be123c; font-weight: 600; background: rgba(255,255,255,0.7); padding: 4px 10px; border-radius: 20px;">
            Ø§Ø¶ØºØ· Ù„Ù„ØªÙØ§ØµÙŠÙ„ âœ¨
          </span>
        </div>
      </div>
    `;

    this.container.onclick = () => {
      if (this.onClickDetail) {
        this.onClickDetail(this.calculate());
      }
    };
  }

  update() {
    const daysEl = document.getElementById('heart-days-num');
    if (daysEl) {
      const time = this.calculate();
      daysEl.textContent = time.totalDays;
    }
  }
}
// 4 Realtime Two-Player Games: XO, Connect 4, Car Racing, and Quiz Trivia

import { realtimeStore } from '../store/realtimeStore.js';
import { QUESTIONS_BANK } from '../data/questionsData.js';

export class GamesManager {
  constructor(currentUser, onCoinEarned) {
    this.currentUser = currentUser; // 'ahmed' or 'rody'
    this.onCoinEarned = onCoinEarned;
    this.quizTimer = null;
  }

  // -------------------------------------------------------------
  // GAME 1: XO (TIC TAC TOE)
  // -------------------------------------------------------------
  initXO(container) {
    const session = realtimeStore.state.gameSessions.xo || {
      board: Array(9).fill(null),
      turn: 'ahmed',
      winner: null
    };

    const render = () => {
      const { board, turn, winner } = realtimeStore.state.gameSessions.xo;
      const isMyTurn = turn === this.currentUser && !winner;
      const statusText = winner 
        ? (winner === 'draw' ? 'ØªØ¹Ø§Ø¯Ù„ Ø±Ø§Ø¦Ø¹ Ø¨ÙŠÙ†ÙƒÙ…Ø§! ðŸ¤' : `Ø§Ù„ÙØ§Ø¦Ø²: ${winner === 'ahmed' ? 'Ahmed ðŸ†' : 'Rody ðŸ†'} (+20 ÙƒÙˆÙŠÙ†Ø²)`)
        : `Ø§Ù„Ø¯ÙˆØ± Ø§Ù„Ø­Ø§Ù„ÙŠ: ${turn === 'ahmed' ? 'Ahmed (X)' : 'Rody (O)'} ${isMyTurn ? 'ðŸ‘‰ Ø¯ÙˆØ±Ùƒ Ø§Ù„Ø¢Ù†!' : 'â³ Ø§Ù†ØªØ¸Ø±...'}`;

      container.innerHTML = `
        <div class="glass-card" style="padding: 20px; text-align: center;">
          <div style="font-size: 18px; font-weight: 700; margin-bottom: 6px;">Ù„Ø¹Ø¨Ø© Ø¥ÙƒØ³ Ø£Ùˆ (XO) âš”ï¸</div>
          <div style="font-size: 14px; color: var(--primary); font-weight: 600; margin-bottom: 16px;">${statusText}</div>
          
          <div style="
            display: grid;
            grid-template-columns: repeat(3, 80px);
            gap: 10px;
            justify-content: center;
            margin: 0 auto 16px;
          ">
            ${board.map((cell, idx) => `
              <button class="glass-btn-secondary xo-cell" data-idx="${idx}" style="
                width: 80px;
                height: 80px;
                font-size: 32px;
                font-weight: 800;
                color: ${cell === 'X' ? '#7c3aed' : '#f43f5e'};
                border-radius: 16px;
                cursor: ${!cell && isMyTurn ? 'pointer' : 'default'};
              ">${cell || ''}</button>
            `).join('')}
          </div>

          <button id="xo-restart-btn" class="glass-btn" style="padding: 8px 18px; font-size: 13px;">
            Ø¥Ø¹Ø§Ø¯Ø© Ø¨Ø¯Ø¡ Ø§Ù„Ù„Ø¹Ø¨Ø© ðŸ”„
          </button>
        </div>
      `;

      container.querySelectorAll('.xo-cell').forEach(btn => {
        btn.onclick = () => {
          const idx = parseInt(btn.dataset.idx, 10);
          this.makeXOMove(idx);
        };
      });

      const restartBtn = container.querySelector('#xo-restart-btn');
      if (restartBtn) {
        restartBtn.onclick = () => this.resetXO();
      }
    };

    render();
    return realtimeStore.subscribe('game_updated', (data) => {
      if (data.gameKey === 'xo') render();
    });
  }

  makeXOMove(index) {
    const session = realtimeStore.state.gameSessions.xo;
    if (session.board[index] || session.winner || session.turn !== this.currentUser) return;

    const symbol = this.currentUser === 'ahmed' ? 'X' : 'O';
    session.board[index] = symbol;

    // Check winner
    const lines = [
      [0,1,2],[3,4,5],[6,7,8],
      [0,3,6],[1,4,7],[2,5,8],
      [0,4,8],[2,4,6]
    ];
    let winFound = false;
    for (const [a, b, c] of lines) {
      if (session.board[a] && session.board[a] === session.board[b] && session.board[a] === session.board[c]) {
        session.winner = this.currentUser;
        winFound = true;
        this.rewardWinner(this.currentUser, 20);
        break;
      }
    }

    if (!winFound && session.board.every(cell => cell !== null)) {
      session.winner = 'draw';
    } else if (!winFound) {
      session.turn = this.currentUser === 'ahmed' ? 'rody' : 'ahmed';
    }

    realtimeStore.updateGameSession('xo', session);
  }

  resetXO() {
    realtimeStore.updateGameSession('xo', {
      board: Array(9).fill(null),
      turn: 'ahmed',
      winner: null
    });
  }

  // -------------------------------------------------------------
  // GAME 2: CONNECT FOUR (4 Ø¹Ù„Ù‰ Ø§Ù„ØªÙˆØ§Ù„ÙŠ)
  // -------------------------------------------------------------
  initConnectFour(container) {
    const render = () => {
      const session = realtimeStore.state.gameSessions.connect4;
      const isMyTurn = session.turn === this.currentUser && !session.winner;
      const status = session.winner
        ? `Ø§Ù„ÙØ§Ø¦Ø²: ${session.winner === 'ahmed' ? 'Ahmed ðŸ”´' : 'Rody ðŸŸ¡'} (+25 ÙƒÙˆÙŠÙ†Ø²)`
        : `Ø§Ù„Ø¯ÙˆØ±: ${session.turn === 'ahmed' ? 'Ahmed' : 'Rody'} ${isMyTurn ? 'ðŸ‘‰ Ø¯ÙˆØ±Ùƒ Ø¥Ø³Ù‚Ø§Ø· Ø§Ù„Ù‚Ø±Øµ!' : 'â³ Ø§Ù†ØªØ¸Ø±...'}`;

      container.innerHTML = `
        <div class="glass-card" style="padding: 20px; text-align: center;">
          <div style="font-size: 18px; font-weight: 700; margin-bottom: 6px;">Connect Four ðŸ”´ðŸŸ¡</div>
          <div style="font-size: 14px; color: var(--primary); font-weight: 600; margin-bottom: 14px;">${status}</div>

          <!-- Drop Column Buttons -->
          <div style="display: grid; grid-template-columns: repeat(7, 38px); gap: 6px; justify-content: center; margin-bottom: 6px;">
            ${[0,1,2,3,4,5,6].map(col => `
              <button class="c4-drop-btn glass-btn" data-col="${col}" style="
                padding: 4px; font-size: 12px; height: 32px; border-radius: 8px;
                opacity: ${isMyTurn ? '1' : '0.4'};
              " ${!isMyTurn ? 'disabled' : ''}>â¬‡ï¸</button>
            `).join('')}
          </div>

          <!-- 6 Rows x 7 Columns Grid -->
          <div style="
            background: rgba(124, 58, 237, 0.15);
            padding: 10px;
            border-radius: 16px;
            display: inline-block;
            border: 2px solid var(--primary-light);
          ">
            ${session.board.map((row) => `
              <div style="display: grid; grid-template-columns: repeat(7, 38px); gap: 6px; margin-bottom: 6px;">
                ${row.map(cell => `
                  <div style="
                    width: 38px;
                    height: 38px;
                    border-radius: 50%;
                    background: ${cell === 'ahmed' ? '#f43f5e' : cell === 'rody' ? '#facc15' : 'rgba(255,255,255,0.7)'};
                    border: 1px solid rgba(0,0,0,0.1);
                    box-shadow: inset 0 2px 4px rgba(0,0,0,0.15);
                  "></div>
                `).join('')}
              </div>
            `).join('')}
          </div>

          <div style="margin-top: 14px;">
            <button id="c4-restart" class="glass-btn" style="padding: 8px 18px; font-size: 13px;">Ø¥Ø¹Ø§Ø¯Ø© Ø§Ù„Ù„Ø¹Ø¨Ø© ðŸ”„</button>
          </div>
        </div>
      `;

      container.querySelectorAll('.c4-drop-btn').forEach(btn => {
        btn.onclick = () => this.dropConnectFour(parseInt(btn.dataset.col, 10));
      });

      const rBtn = container.querySelector('#c4-restart');
      if (rBtn) rBtn.onclick = () => this.resetConnectFour();
    };

    render();
    return realtimeStore.subscribe('game_updated', (data) => {
      if (data.gameKey === 'connect4') render();
    });
  }

  dropConnectFour(col) {
    const session = realtimeStore.state.gameSessions.connect4;
    if (session.winner || session.turn !== this.currentUser) return;

    // Find lowest available row in column
    let targetRow = -1;
    for (let r = 5; r >= 0; r--) {
      if (!session.board[r][col]) {
        targetRow = r;
        break;
      }
    }
    if (targetRow === -1) return; // Column full

    session.board[targetRow][col] = this.currentUser;

    // Simple check win logic
    if (this.checkC4Win(session.board, targetRow, col, this.currentUser)) {
      session.winner = this.currentUser;
      this.rewardWinner(this.currentUser, 25);
    } else {
      session.turn = this.currentUser === 'ahmed' ? 'rody' : 'ahmed';
    }

    realtimeStore.updateGameSession('connect4', session);
  }

  checkC4Win(b, r, c, player) {
    const directions = [[0,1], [1,0], [1,1], [1,-1]];
    for (const [dr, dc] of directions) {
      let count = 1;
      // positive
      let step = 1;
      while (r + dr*step >= 0 && r + dr*step < 6 && c + dc*step >= 0 && c + dc*step < 7 && b[r + dr*step][c + dc*step] === player) {
        count++; step++;
      }
      // negative
      step = 1;
      while (r - dr*step >= 0 && r - dr*step < 6 && c - dc*step >= 0 && c - dc*step < 7 && b[r - dr*step][c - dc*step] === player) {
        count++; step++;
      }
      if (count >= 4) return true;
    }
    return false;
  }

  resetConnectFour() {
    realtimeStore.updateGameSession('connect4', {
      board: Array(6).fill(null).map(() => Array(7).fill(null)),
      turn: 'ahmed',
      winner: null
    });
  }

  // -------------------------------------------------------------
  // GAME 3: CAR RACING (Ø³Ø¨Ø§Ù‚ Ø§Ù„Ø³ÙŠØ§Ø±Ø§Øª Ø§Ù„Ø®ÙÙŠÙ)
  // -------------------------------------------------------------
  initRacing(container) {
    const render = () => {
      const session = realtimeStore.state.gameSessions.racing || { ahmedPos: 0, rodyPos: 0, winner: null };
      const status = session.winner
        ? `Ø§Ù„ÙØ§Ø¦Ø² Ø¨Ø§Ù„Ø³Ø¨Ø§Ù‚: ${session.winner === 'ahmed' ? 'Ahmed ðŸŽï¸' : 'Rody ðŸŽï¸'} (+30 ÙƒÙˆÙŠÙ†Ø²)`
        : 'Ø§Ø¶ØºØ· Ø¨Ø£Ø³Ø±Ø¹ Ù…Ø§ ÙŠÙ…ÙƒÙ†Ùƒ Ø¹Ù„Ù‰ Ø²Ø± Ø§Ù„Ø³Ø±Ø¹Ø© Ù„Ù„ÙˆØµÙˆÙ„ Ù„Ø®Ø· Ø§Ù„Ù†Ù‡Ø§ÙŠØ©! ðŸ';

      container.innerHTML = `
        <div class="glass-card" style="padding: 20px; text-align: center;">
          <div style="font-size: 18px; font-weight: 700; margin-bottom: 6px;">Ø³Ø¨Ø§Ù‚ Ø§Ù„Ø³ÙŠØ§Ø±Ø§Øª Ø§Ù„Ø³Ø±ÙŠØ¹ ðŸŽï¸ðŸ</div>
          <div style="font-size: 13px; color: var(--text-muted); margin-bottom: 16px;">${status}</div>

          <!-- Track -->
          <div style="background: rgba(0,0,0,0.06); border-radius: 14px; padding: 14px; margin-bottom: 20px;">
            <!-- Ahmed Lane -->
            <div style="margin-bottom: 14px; text-align: right;">
              <span style="font-weight: 600; font-size: 13px;">Ahmed ðŸŽï¸ (Ø£Ø²Ø±Ù‚)</span>
              <div style="background: rgba(255,255,255,0.8); height: 26px; border-radius: 13px; position: relative; overflow: hidden; margin-top: 4px;">
                <div style="
                  position: absolute;
                  right: 0;
                  top: 0;
                  bottom: 0;
                  width: ${Math.min(100, session.ahmedPos)}%;
                  background: linear-gradient(90deg, #3b82f6, #60a5fa);
                  border-radius: 13px;
                  transition: width 0.15s ease;
                "></div>
              </div>
            </div>

            <!-- Rody Lane -->
            <div style="text-align: right;">
              <span style="font-weight: 600; font-size: 13px;">Rody ðŸŽï¸ (ÙˆØ±Ø¯ÙŠ)</span>
              <div style="background: rgba(255,255,255,0.8); height: 26px; border-radius: 13px; position: relative; overflow: hidden; margin-top: 4px;">
                <div style="
                  position: absolute;
                  right: 0;
                  top: 0;
                  bottom: 0;
                  width: ${Math.min(100, session.rodyPos)}%;
                  background: linear-gradient(90deg, #ec4899, #f472b6);
                  border-radius: 13px;
                  transition: width 0.15s ease;
                "></div>
              </div>
            </div>
          </div>

          <div style="display: flex; gap: 12px; justify-content: center;">
            <button id="racing-tap-btn" class="glass-btn" style="padding: 12px 28px; font-size: 16px;" ${session.winner ? 'disabled' : ''}>
              Ø¯ÙˆØ³ Ø¨Ù†Ø²ÙŠÙ†! ðŸš€ðŸ’¨
            </button>
            <button id="racing-reset-btn" class="glass-btn-secondary glass-btn" style="padding: 12px 20px;">
              Ø³Ø¨Ø§Ù‚ Ø¬Ø¯ÙŠØ¯ ðŸ”„
            </button>
          </div>
        </div>
      `;

      const tapBtn = container.querySelector('#racing-tap-btn');
      if (tapBtn) {
        tapBtn.onclick = () => this.stepRacing();
      }
      const resetBtn = container.querySelector('#racing-reset-btn');
      if (resetBtn) {
        resetBtn.onclick = () => this.resetRacing();
      }
    };

    render();
    return realtimeStore.subscribe('game_updated', (data) => {
      if (data.gameKey === 'racing') render();
    });
  }

  stepRacing() {
    const session = realtimeStore.state.gameSessions.racing;
    if (session.winner) return;

    if (this.currentUser === 'ahmed') {
      session.ahmedPos += 5;
      if (session.ahmedPos >= 100) {
        session.winner = 'ahmed';
        this.rewardWinner('ahmed', 30);
      }
    } else {
      session.rodyPos += 5;
      if (session.rodyPos >= 100) {
        session.winner = 'rody';
        this.rewardWinner('rody', 30);
      }
    }

    realtimeStore.updateGameSession('racing', session);
  }

  resetRacing() {
    realtimeStore.updateGameSession('racing', {
      ahmedPos: 0,
      rodyPos: 0,
      winner: null
    });
  }

  // -------------------------------------------------------------
  // GAME 4: QUESTIONS TRIVIA (Ù…Ø³Ø§Ø¨Ù‚Ø© Ø§Ù„Ø£Ø³Ø¦Ù„Ø©)
  // -------------------------------------------------------------
  initQuiz(container) {
    const render = () => {
      const session = realtimeStore.state.gameSessions.quiz;
      const q = QUESTIONS_BANK[session.currentQuestionIndex % QUESTIONS_BANK.length];
      const hasMyAnswer = this.currentUser === 'ahmed' ? session.ahmedAnswer !== null : session.rodyAnswer !== null;

      container.innerHTML = `
        <div class="glass-card" style="padding: 22px;">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px;">
            <span class="glass-btn-secondary" style="padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 600;">
              ðŸ·ï¸ ${q.category}
            </span>
            <div style="font-size: 13px; font-weight: 700; color: var(--primary);">
              Ø§Ù„Ù†Ù‚Ø§Ø·: Ahmed (${session.scores.ahmed}) vs Rody (${session.scores.rody})
            </div>
          </div>

          <div style="font-size: 17px; font-weight: 700; line-height: 1.6; margin-bottom: 20px; text-align: right;">
            ${q.question}
          </div>

          <!-- Options Grid -->
          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 20px;">
            ${q.options.map((opt, optIdx) => {
              let bg = 'var(--surface-glass-card)';
              if (session.revealed) {
                if (optIdx === q.correct) bg = 'rgba(16, 185, 129, 0.25); border-color: #10b981; font-weight: 700;';
              }
              return `
                <button class="glass-btn-secondary quiz-opt-btn" data-opt="${optIdx}" style="
                  padding: 14px;
                  border-radius: 14px;
                  text-align: right;
                  font-size: 14px;
                  background: ${bg};
                " ${hasMyAnswer || session.revealed ? 'disabled' : ''}>
                  ${opt}
                </button>
              `;
            }).join('')}
          </div>

          <!-- Status / Results -->
          <div style="text-align: center; font-size: 13px; margin-bottom: 16px; color: var(--text-muted);">
            ${session.revealed 
              ? `Ø¥Ø¬Ø§Ø¨Ø© Ahmed: ${q.options[session.ahmedAnswer] || 'Ù„Ù… ÙŠØ¬Ø¨'} | Ø¥Ø¬Ø§Ø¨Ø© Rody: ${q.options[session.rodyAnswer] || 'Ù„Ù… ÙŠØ¬Ø¨'}`
              : (hasMyAnswer ? 'ØªÙ… ØªØ³Ø¬ÙŠÙ„ Ø¥Ø¬Ø§Ø¨ØªÙƒ Ø§Ù„Ø³Ø±ÙŠØ© Ø¨Ù†Ø¬Ø§Ø­ ðŸ”’ ÙÙŠ Ø§Ù†ØªØ¸Ø§Ø± Ø§Ù„Ø·Ø±Ù Ø§Ù„Ø¢Ø®Ø±...' : 'Ø§Ø®ØªØ± Ø¥Ø¬Ø§Ø¨ØªÙƒ Ø§Ù„Ø¢Ù†!')
            }
          </div>

          <div style="display: flex; gap: 10px; justify-content: center;">
            <button id="quiz-reveal-btn" class="glass-btn" style="font-size: 13px; padding: 8px 18px;" ${session.revealed ? 'disabled' : ''}>
              ÙƒØ´Ù Ø§Ù„Ø¥Ø¬Ø§Ø¨Ø§Øª ðŸ‘ï¸
            </button>
            <button id="quiz-next-btn" class="glass-btn-secondary glass-btn" style="font-size: 13px; padding: 8px 18px;">
              Ø§Ù„Ø³Ø¤Ø§Ù„ Ø§Ù„ØªØ§Ù„ÙŠ âž¡ï¸
            </button>
          </div>
        </div>
      `;

      container.querySelectorAll('.quiz-opt-btn').forEach(btn => {
        btn.onclick = () => {
          this.submitQuizAnswer(parseInt(btn.dataset.opt, 10));
        };
      });

      const revBtn = container.querySelector('#quiz-reveal-btn');
      if (revBtn) revBtn.onclick = () => this.revealQuiz();

      const nextBtn = container.querySelector('#quiz-next-btn');
      if (nextBtn) nextBtn.onclick = () => this.nextQuiz();
    };

    render();
    return realtimeStore.subscribe('game_updated', (data) => {
      if (data.gameKey === 'quiz') render();
    });
  }

  submitQuizAnswer(optIndex) {
    const session = realtimeStore.state.gameSessions.quiz;
    if (this.currentUser === 'ahmed') session.ahmedAnswer = optIndex;
    else session.rodyAnswer = optIndex;

    realtimeStore.updateGameSession('quiz', session);
  }

  revealQuiz() {
    const session = realtimeStore.state.gameSessions.quiz;
    session.revealed = true;
    const q = QUESTIONS_BANK[session.currentQuestionIndex % QUESTIONS_BANK.length];

    if (session.ahmedAnswer === q.correct) {
      session.scores.ahmed += q.points;
      this.rewardWinner('ahmed', q.points);
    }
    if (session.rodyAnswer === q.correct) {
      session.scores.rody += q.points;
      this.rewardWinner('rody', q.points);
    }

    realtimeStore.updateGameSession('quiz', session);
  }

  nextQuiz() {
    const session = realtimeStore.state.gameSessions.quiz;
    session.currentQuestionIndex++;
    session.ahmedAnswer = null;
    session.rodyAnswer = null;
    session.revealed = false;
    realtimeStore.updateGameSession('quiz', session);
  }

  rewardWinner(userId, coins) {
    realtimeStore.adjustCoins(userId, coins);
    if (this.onCoinEarned) this.onCoinEarned(userId, coins);
  }
}
// Nexus Web App â€” Main Application Module
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
          <span style="font-size: 24px;">âœ¨</span>
          <span style="font-size: 20px; font-weight: 800; background: linear-gradient(135deg, var(--primary), var(--accent-pink)); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">Nexus</span>
        </div>
        <div style="display: flex; align-items: center; gap: 8px;">
          <button id="lang-toggle" class="glass-btn-secondary" style="padding: 6px 10px; border-radius: 10px; font-size: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border);">
            ${this.lang === 'ar' ? 'EN' : 'Ø¹Ø±Ø¨ÙŠ'}
          </button>
          <button id="theme-toggle" class="glass-btn-secondary" style="padding: 6px 10px; border-radius: 10px; font-size: 16px; cursor: pointer; border: 1px solid var(--surface-glass-border);">
            ${this.theme === 'dark' ? 'â˜€ï¸' : 'ðŸŒ™'}
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
        <button class="nav-item active" data-screen="home" title="${this.t('Ø§Ù„Ø±Ø¦ÙŠØ³ÙŠØ©','Home')}">
          <span style="font-size: 22px;">ðŸ </span>
          <span class="nav-label">${this.t('Ø§Ù„Ø±Ø¦ÙŠØ³ÙŠØ©','Home')}</span>
        </button>
        <button class="nav-item" data-screen="notifications" title="${this.t('Ø§Ù„Ø¥Ø´Ø¹Ø§Ø±Ø§Øª','Notifications')}">
          <span style="font-size: 22px;">ðŸ””</span>
          <span class="nav-label">${this.t('Ø¥Ø´Ø¹Ø§Ø±Ø§Øª','Alerts')}</span>
        </button>
        <button class="nav-item" data-screen="tasks" title="${this.t('Ø§Ù„Ù…Ù‡Ø§Ù…','Tasks')}">
          <span style="font-size: 22px;">âœ…</span>
          <span class="nav-label">${this.t('Ø§Ù„Ù…Ù‡Ø§Ù…','Tasks')}</span>
        </button>
        <button class="nav-item" data-screen="entertainment" title="${this.t('ØªØ±ÙÙŠÙ‡','Fun')}">
          <span style="font-size: 22px;">ðŸŽ®</span>
          <span class="nav-label">${this.t('ØªØ±ÙÙŠÙ‡','Fun')}</span>
        </button>
        <button class="nav-item" data-screen="settings" title="${this.t('Ø§Ù„Ø¥Ø¹Ø¯Ø§Ø¯Ø§Øª','Settings')}">
          <span style="font-size: 22px;">âš™ï¸</span>
          <span class="nav-label">${this.t('Ø§Ù„Ù…Ø²ÙŠØ¯','More')}</span>
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
          ${this.t('Ù…Ø±Ø­Ø¨Ø§Ù‹ Ø¨Ùƒ ÙÙŠ Nexus','Welcome to Nexus')} ðŸ‘‹
        </h1>
        <p style="color: var(--text-muted); font-size: 14px;">
          ${this.t('Ù„ÙˆØ­Ø© ØªØ­ÙƒÙ…Ùƒ Ø§Ù„Ø´Ø®ØµÙŠØ© Ø§Ù„Ø°ÙƒÙŠØ©','Your smart personal dashboard')}
        </p>
      </div>

      <!-- Quick Stats Row -->
      <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; margin-bottom: 18px;">
        <div class="glass-card" style="padding: 14px; text-align: center;">
          <div style="font-size: 24px; font-weight: 800; color: var(--primary);">${s.notifications.length}</div>
          <div style="font-size: 11px; color: var(--text-muted);">${this.t('Ø¥Ø´Ø¹Ø§Ø±','Alerts')}</div>
        </div>
        <div class="glass-card" style="padding: 14px; text-align: center;">
          <div style="font-size: 24px; font-weight: 800; color: var(--accent-emerald);">${completedCount}/${todayTasks.length}</div>
          <div style="font-size: 11px; color: var(--text-muted);">${this.t('Ù…Ù‡Ø§Ù… Ø§Ù„ÙŠÙˆÙ…','Today')}</div>
        </div>
        <div class="glass-card" style="padding: 14px; text-align: center;">
          <div style="font-size: 24px; font-weight: 800; color: var(--accent-amber);">${s.aiApps.filter(a => a.pinned).length}</div>
          <div style="font-size: 11px; color: var(--text-muted);">AI</div>
        </div>
      </div>

      <!-- Prayer Times Quick Card -->
      <div class="glass-card" style="padding: 16px; margin-bottom: 16px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
          <span style="font-weight: 700; font-size: 15px;">ðŸ•Œ ${this.t('Ù…ÙˆØ§Ù‚ÙŠØª Ø§Ù„ØµÙ„Ø§Ø©','Prayer Times')}</span>
        </div>
        <div style="display: grid; grid-template-columns: repeat(5, 1fr); gap: 6px; text-align: center;">
          ${[
            { name: this.t('Ø§Ù„ÙØ¬Ø±','Fajr'), time: prayerTimes.fajr },
            { name: this.t('Ø§Ù„Ø¸Ù‡Ø±','Dhuhr'), time: prayerTimes.dhuhr },
            { name: this.t('Ø§Ù„Ø¹ØµØ±','Asr'), time: prayerTimes.asr },
            { name: this.t('Ø§Ù„Ù…ØºØ±Ø¨','Maghrib'), time: prayerTimes.maghrib },
            { name: this.t('Ø§Ù„Ø¹Ø´Ø§Ø¡','Isha'), time: prayerTimes.isha }
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
          <div style="font-size: 28px; margin-bottom: 6px;">ðŸ¤–</div>
          <div style="font-weight: 700; font-size: 14px;">${this.t('Ù…Ø±ÙƒØ² AI','AI Hub')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">${s.aiApps.length} ${this.t('ØªØ·Ø¨ÙŠÙ‚','apps')}</div>
        </div>
        <div class="glass-card" style="padding: 16px; cursor: pointer;" onclick="nexusApp.navigate('schedule')">
          <div style="font-size: 28px; margin-bottom: 6px;">ðŸ“…</div>
          <div style="font-weight: 700; font-size: 14px;">${this.t('Ø§Ù„Ø¬Ø¯ÙˆÙ„','Schedule')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">${s.schedules.length} ${this.t('Ù…ÙˆØ¹Ø¯','events')}</div>
        </div>
        <div class="glass-card" style="padding: 16px; cursor: pointer;" onclick="nexusApp.navigate('entertainment')">
          <div style="font-size: 28px; margin-bottom: 6px;">ðŸŽ®</div>
          <div style="font-weight: 700; font-size: 14px;">${this.t('ØªØ±ÙÙŠÙ‡','Entertainment')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">${this.t('Ø£Ù„Ø¹Ø§Ø¨ ÙˆØ¯Ø±Ø¯Ø´Ø©','Games & Chat')}</div>
        </div>
        <div class="glass-card" style="padding: 16px; cursor: pointer;" onclick="nexusApp.navigate('search')">
          <div style="font-size: 28px; margin-bottom: 6px;">ðŸ”</div>
          <div style="font-weight: 700; font-size: 14px;">${this.t('Ø§Ù„Ø¨Ø­Ø«','Search')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">${this.t('Ø¨Ø­Ø« Ø´Ø§Ù…Ù„','Unified')}</div>
        </div>
      </div>

      <!-- Pinned AI Apps -->
      <div style="margin-bottom: 16px;">
        <h3 style="font-size: 15px; font-weight: 700; margin-bottom: 10px;">${this.t('ØªØ·Ø¨ÙŠÙ‚Ø§Øª AI Ø§Ù„Ù…Ø«Ø¨ØªØ©','Pinned AI')}</h3>
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
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">ðŸ”” ${this.t('Ø§Ù„Ø¥Ø´Ø¹Ø§Ø±Ø§Øª','Notifications')}</h2>
      ${notifs.length === 0 ? `
        <div class="glass-card" style="padding: 40px; text-align: center;">
          <div style="font-size: 48px; margin-bottom: 12px;">ðŸ’¬</div>
          <div style="font-size: 15px; color: var(--text-muted);">${this.t('Ù„Ø³Ù‡ Ù…ÙÙŠØ´ Ø¥Ø´Ø¹Ø§Ø±Ø§Øª Ù‡Ù†Ø§','No notifications yet')}</div>
        </div>
      ` : notifs.map(n => `
        <div class="glass-card" style="padding: 14px; margin-bottom: 10px; display: flex; gap: 12px; align-items: flex-start;">
          <div style="
            width: 40px; height: 40px; border-radius: 12px;
            background: ${n.priority === 'HIGH' ? 'rgba(244,63,94,0.15)' : n.priority === 'MEDIUM' ? 'rgba(245,158,11,0.15)' : 'rgba(100,116,139,0.1)'};
            display: flex; align-items: center; justify-content: center; flex-shrink: 0; font-size: 18px;
          ">${n.priority === 'HIGH' ? 'ðŸ”´' : n.priority === 'MEDIUM' ? 'ðŸŸ¡' : 'âšª'}</div>
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
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">ðŸ¤– ${this.t('Ù…Ø±ÙƒØ² Ø§Ù„Ø°ÙƒØ§Ø¡ Ø§Ù„Ø§ØµØ·Ù†Ø§Ø¹ÙŠ','AI Hub')}</h2>
      <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px;">
        ${apps.map(app => `
          <a href="${app.url}" target="_blank" rel="noopener" class="glass-card" style="padding: 18px; text-decoration: none; color: inherit; text-align: center;">
            <div style="font-size: 36px; margin-bottom: 8px;">${app.emoji}</div>
            <div style="font-weight: 700; font-size: 15px; margin-bottom: 4px;">${app.name}</div>
            <div style="font-size: 12px; color: var(--text-muted); line-height: 1.4;">${app.desc}</div>
            ${app.pinned ? '<div style="margin-top: 6px; font-size: 11px; color: var(--accent-amber);">ðŸ“Œ Ù…Ø«Ø¨Øª</div>' : ''}
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
      { key: 'today', label: this.t('Ø§Ù„ÙŠÙˆÙ…','Today'), icon: 'ðŸ“‹' },
      { key: 'upcoming', label: this.t('Ø§Ù„Ù‚Ø§Ø¯Ù…Ø©','Upcoming'), icon: 'ðŸ“…' },
      { key: 'done', label: this.t('Ø§Ù„Ù…Ù†Ø¬Ø²Ø©','Done'), icon: 'âœ…' }
    ];

    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">âœ… ${this.t('Ø§Ù„Ù…Ù‡Ø§Ù…','Tasks')}</h2>

      <!-- Add Task -->
      <div class="glass-card" style="padding: 14px; margin-bottom: 16px; display: flex; gap: 10px;">
        <input id="new-task-input" type="text" placeholder="${this.t('Ø£Ø¶Ù Ù…Ù‡Ù…Ø© Ø¬Ø¯ÙŠØ¯Ø©...','Add new task...')}" style="
          flex: 1; padding: 10px 14px; border-radius: 12px; border: 1px solid var(--surface-glass-border);
          background: var(--surface-glass); color: var(--text-main); font-size: 14px; outline: none;
        ">
        <button id="add-task-btn" class="glass-btn" style="padding: 10px 16px;">âž•</button>
      </div>

      ${categories.map(cat => {
        const catTasks = tasks.filter(t => cat.key === 'done' ? t.completed : (!t.completed && t.category === cat.key));
        return `
          <div style="margin-bottom: 18px;">
            <h3 style="font-size: 15px; font-weight: 700; margin-bottom: 8px;">${cat.icon} ${cat.label} (${catTasks.length})</h3>
            ${catTasks.length === 0 ? `<div style="text-align: center; padding: 16px; font-size: 13px; color: var(--text-muted);">${this.t('Ù„Ø§ Ù…Ù‡Ø§Ù… Ù‡Ù†Ø§ Ø­Ø§Ù„ÙŠØ§Ù‹','No tasks here')}</div>` :
            catTasks.map(task => `
              <div class="glass-card" style="padding: 12px 14px; margin-bottom: 8px; display: flex; align-items: center; gap: 10px;">
                <button class="task-check-btn" data-id="${task.id}" style="
                  width: 24px; height: 24px; border-radius: 8px; border: 2px solid ${task.completed ? 'var(--accent-emerald)' : 'var(--text-muted)'};
                  background: ${task.completed ? 'var(--accent-emerald)' : 'transparent'}; cursor: pointer; display: flex; align-items: center; justify-content: center;
                  color: white; font-size: 14px; flex-shrink: 0;
                ">${task.completed ? 'âœ“' : ''}</button>
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
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">ðŸ“… ${this.t('Ø§Ù„Ø¬Ø¯ÙˆÙ„ ÙˆØ§Ù„Ù…ÙˆØ§Ø¹ÙŠØ¯','Schedule')}</h2>
      ${schedules.length === 0 ? `
        <div class="glass-card" style="padding: 40px; text-align: center;">
          <div style="font-size: 48px; margin-bottom: 12px;">ðŸ“…</div>
          <div style="font-size: 15px; color: var(--text-muted);">${this.t('Ù„Ø§ Ù…ÙˆØ§Ø¹ÙŠØ¯ Ø­Ø§Ù„ÙŠØ§Ù‹','No events yet')}</div>
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
            <div style="font-size: 12px; color: var(--text-muted);">${s.category} â€” ${s.note}</div>
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
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">ðŸ” ${this.t('Ø§Ù„Ø¨Ø­Ø« Ø§Ù„Ø´Ø§Ù…Ù„','Unified Search')}</h2>
      <div class="glass-card" style="padding: 14px; margin-bottom: 16px;">
        <input id="search-input" type="text" placeholder="${this.t('Ø§Ø¨Ø­Ø« ÙÙŠ ÙƒÙ„ Ø´ÙŠØ¡...','Search everything...')}" style="
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
        html += `<h4 style="margin: 10px 0 6px; font-size: 13px; color: var(--text-muted);">ðŸ”” ${this.t('Ø¥Ø´Ø¹Ø§Ø±Ø§Øª','Notifications')}</h4>`;
        nots.forEach(n => { html += `<div class="glass-card" style="padding: 10px; margin-bottom: 6px; font-size: 13px;"><b>${n.appName}:</b> ${n.title}</div>`; });
      }
      // Search tasks
      const tsks = s.tasks.filter(t => t.title.toLowerCase().includes(q));
      if (tsks.length) {
        html += `<h4 style="margin: 10px 0 6px; font-size: 13px; color: var(--text-muted);">âœ… ${this.t('Ù…Ù‡Ø§Ù…','Tasks')}</h4>`;
        tsks.forEach(t => { html += `<div class="glass-card" style="padding: 10px; margin-bottom: 6px; font-size: 13px;">${t.completed ? 'â˜‘ï¸' : 'â¬œ'} ${t.title}</div>`; });
      }
      // Search AI apps
      const ais = s.aiApps.filter(a => a.name.toLowerCase().includes(q));
      if (ais.length) {
        html += `<h4 style="margin: 10px 0 6px; font-size: 13px; color: var(--text-muted);">ðŸ¤– AI</h4>`;
        ais.forEach(a => { html += `<div class="glass-card" style="padding: 10px; margin-bottom: 6px; font-size: 13px;">${a.emoji} ${a.name}</div>`; });
      }

      if (!html) html = `<div style="text-align: center; padding: 20px; color: var(--text-muted);">${this.t('Ù„Ø§ Ù†ØªØ§Ø¦Ø¬','No results')}</div>`;
      results.innerHTML = html;
    });
  }

  // =========================================================
  // SETTINGS SCREEN
  // =========================================================
  renderSettings(el) {
    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">âš™ï¸ ${this.t('Ø§Ù„Ø¥Ø¹Ø¯Ø§Ø¯Ø§Øª','Settings')}</h2>

      <div class="glass-card" style="padding: 16px; margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center; cursor: pointer;" onclick="nexusApp.toggleTheme(); nexusApp.renderApp();">
        <span style="font-weight: 600;">${this.theme === 'dark' ? 'â˜€ï¸' : 'ðŸŒ™'} ${this.t('Ø§Ù„ÙˆØ¶Ø¹ Ø§Ù„Ù„ÙŠÙ„ÙŠ','Dark Mode')}</span>
        <span style="font-size: 13px; color: var(--primary); font-weight: 700;">${this.theme === 'dark' ? this.t('Ù…ÙØ¹Ù„','ON') : this.t('Ù…Ø¹Ø·Ù„','OFF')}</span>
      </div>

      <div class="glass-card" style="padding: 16px; margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center; cursor: pointer;" onclick="nexusApp.toggleLang();">
        <span style="font-weight: 600;">ðŸŒ ${this.t('Ø§Ù„Ù„ØºØ©','Language')}</span>
        <span style="font-size: 13px; color: var(--primary); font-weight: 700;">${this.lang === 'ar' ? 'Ø§Ù„Ø¹Ø±Ø¨ÙŠØ©' : 'English'}</span>
      </div>

      <div class="glass-card" style="padding: 16px; margin-bottom: 12px; cursor: pointer;" onclick="nexusApp.navigate('aihub')">
        <span style="font-weight: 600;">ðŸ¤– ${this.t('Ù…Ø±ÙƒØ² Ø§Ù„Ø°ÙƒØ§Ø¡ Ø§Ù„Ø§ØµØ·Ù†Ø§Ø¹ÙŠ','AI Hub')}</span>
      </div>

      <div class="glass-card" style="padding: 16px; margin-bottom: 12px; cursor: pointer;" onclick="nexusApp.navigate('schedule')">
        <span style="font-weight: 600;">ðŸ“… ${this.t('Ø§Ù„Ø¬Ø¯ÙˆÙ„ ÙˆØ§Ù„Ù…ÙˆØ§Ø¹ÙŠØ¯','Schedule')}</span>
      </div>

      <div class="glass-card" style="padding: 16px; margin-bottom: 12px;">
        <span style="font-weight: 600;">ðŸ“± ${this.t('Ø¥ØµØ¯Ø§Ø± Ø§Ù„ØªØ·Ø¨ÙŠÙ‚','App Version')}</span>
        <span style="float: left; font-size: 13px; color: var(--text-muted);">Nexus Web v1.0.0</span>
      </div>
    `;
  }

  // =========================================================
  // ENTERTAINMENT HUB
  // =========================================================
  renderEntertainment(el) {
    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">ðŸŽ® ${this.t('ØªØ±ÙÙŠÙ‡','Entertainment')}</h2>

      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
        <div class="glass-card" style="padding: 22px; text-align: center; cursor: pointer;" onclick="nexusApp.navigate('chat-login')">
          <div style="font-size: 36px; margin-bottom: 8px;">ðŸ’¬</div>
          <div style="font-weight: 700; font-size: 15px;">${this.t('Ø¯Ø±Ø¯Ø´Ø© Ø®Ø§ØµØ©','Private Chat')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">Ahmed & Rody</div>
        </div>

        <div class="glass-card" style="padding: 22px; text-align: center; cursor: pointer;" onclick="nexusApp.navigate('games')">
          <div style="font-size: 36px; margin-bottom: 8px;">ðŸŽ®</div>
          <div style="font-weight: 700; font-size: 15px;">${this.t('Ø§Ù„Ø£Ù„Ø¹Ø§Ø¨','Games')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">4 ${this.t('Ø£Ù„Ø¹Ø§Ø¨ Ø«Ù†Ø§Ø¦ÙŠØ©','2P Games')}</div>
        </div>

        <div class="glass-card" style="padding: 22px; text-align: center; cursor: pointer;" onclick="nexusApp.navigate('islamic')">
          <div style="font-size: 36px; margin-bottom: 8px;">ðŸ•Œ</div>
          <div style="font-weight: 700; font-size: 15px;">${this.t('Ø§Ù„Ù‚Ø³Ù… Ø§Ù„Ø¥Ø³Ù„Ø§Ù…ÙŠ','Islamic')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">${this.t('Ø£Ø°ÙƒØ§Ø± ÙˆÙ‚Ø±Ø¢Ù† ÙˆÙ…ÙˆØ§Ù‚ÙŠØª','Azkar & Quran')}</div>
        </div>

        <div class="glass-card" style="padding: 22px; text-align: center; cursor: pointer;" onclick="nexusApp.navigate('mood')">
          <div style="font-size: 36px; margin-bottom: 8px;">ðŸ˜Š</div>
          <div style="font-weight: 700; font-size: 15px;">${this.t('Ù†ÙØ³ÙŠØªÙŠ','My Mood')}</div>
          <div style="font-size: 12px; color: var(--text-muted);">${this.t('Ø´Ø§Ø±Ùƒ Ø­Ø§Ù„ØªÙƒ','Share mood')}</div>
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
        <div style="font-size: 56px; margin-bottom: 16px;">ðŸ’¬</div>
        <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 6px;">${this.t('Ø§Ù„Ø¯Ø±Ø¯Ø´Ø© Ø§Ù„Ø®Ø§ØµØ©','Private Chat')}</h2>
        <p style="color: var(--text-muted); font-size: 14px; margin-bottom: 24px;">${this.t('Ø§Ø®ØªØ± Ø­Ø³Ø§Ø¨Ùƒ ÙˆØ£Ø¯Ø®Ù„ Ø±Ù…Ø² Ø§Ù„Ø¯Ø®ÙˆÙ„','Choose your account')}</p>

        <!-- User Selection -->
        <div style="display: flex; gap: 14px; margin-bottom: 20px;">
          <button id="select-ahmed" class="glass-card" style="padding: 20px 28px; cursor: pointer; text-align: center; border: 2px solid transparent; transition: border-color 0.2s;">
            <div style="font-size: 36px; margin-bottom: 6px;">ðŸ‘¨</div>
            <div style="font-weight: 700;">Ahmed</div>
          </button>
          <button id="select-rody" class="glass-card" style="padding: 20px 28px; cursor: pointer; text-align: center; border: 2px solid transparent; transition: border-color 0.2s;">
            <div style="font-size: 36px; margin-bottom: 6px;">ðŸ‘©</div>
            <div style="font-weight: 700;">Rody</div>
          </button>
        </div>

        <!-- Code Input -->
        <div id="code-section" style="display: none; width: 100%; max-width: 300px; text-align: center;">
          <input id="access-code-input" type="password" placeholder="${this.t('Ø£Ø¯Ø®Ù„ Ø±Ù…Ø² Ø§Ù„Ø¯Ø®ÙˆÙ„','Enter access code')}" style="
            width: 100%; padding: 14px 18px; border-radius: 16px; border: 1px solid var(--surface-glass-border);
            background: var(--surface-glass); color: var(--text-main); font-size: 16px; text-align: center; outline: none; letter-spacing: 4px;
          ">
          <div id="login-error" style="color: var(--accent-pink); font-size: 13px; margin-top: 8px; display: none;"></div>
          <button id="chat-login-btn" class="glass-btn" style="margin-top: 14px; width: 100%; padding: 14px;">
            ${this.t('Ø¯Ø®ÙˆÙ„ Ø§Ù„Ø¯Ø±Ø¯Ø´Ø©','Enter Chat')} ðŸ’¬
          </button>
        </div>

        <button class="glass-btn-secondary" style="margin-top: 20px; padding: 8px 16px; font-size: 13px; cursor: pointer; border: 1px solid var(--surface-glass-border); border-radius: 12px; background: var(--surface-glass); color: var(--text-main);" onclick="nexusApp.navigate('entertainment')">
          ${this.t('Ø±Ø¬ÙˆØ¹','Back')} â†©ï¸
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
      if (!selectedUser) { errorEl.textContent = this.t('Ø§Ø®ØªØ± Ø­Ø³Ø§Ø¨Ùƒ Ø§Ù„Ø£ÙˆÙ„','Select account first'); errorEl.style.display = 'block'; return; }
      if (realtimeStore.verifyCode(selectedUser, code)) {
        this.currentUser = selectedUser;
        this.gamesManager = new GamesManager(selectedUser, (userId, coins) => {
          // Coin earned callback
        });
        this.navigate('chat');
      } else {
        errorEl.textContent = this.t('Ø±Ù…Ø² Ø§Ù„Ø¯Ø®ÙˆÙ„ ØºÙ„Ø·ØŒ Ø­Ø§ÙˆÙ„ ØªØ§Ù†ÙŠ','Wrong code, try again');
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
          <span>ðŸ“</span>
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
            <div style="font-size: 11px; color: var(--accent-emerald);">â— ${this.t('Ù…ØªØµÙ„','Online')}</div>
          </div>
        </div>
        <div style="display: flex; gap: 8px;">
          <button class="glass-btn-secondary" style="padding: 6px 10px; border-radius: 10px; font-size: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border);" onclick="nexusApp.navigate('bubble-styles')">ðŸŽ¨</button>
          <button class="glass-btn-secondary" style="padding: 6px 10px; border-radius: 10px; font-size: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border);" onclick="nexusApp.navigate('entertainment')">â†©ï¸</button>
        </div>
      </div>

      <!-- Messages -->
      <div id="chat-messages" style="display: flex; flex-direction: column; gap: 6px; margin-bottom: 14px; min-height: 200px;">
        ${msgs.length === 0 ? `
          <div style="text-align: center; padding: 40px 0; color: var(--text-muted);">
            <div style="font-size: 48px; margin-bottom: 8px;">ðŸ’¬</div>
            <div>${this.t('Ù„Ø³Ù‡ Ù…ÙÙŠØ´ Ø±Ø³Ø§Ø¦Ù„ Ù‡Ù†Ø§','No messages yet')} ðŸ’¬</div>
          </div>
        ` : msgs.map(msg => {
          const isMe = msg.sender === this.currentUser;
          const senderData = s.users[msg.sender];
          const bubbleStyle = senderData?.bubbleStyle || 'default';
          const time = new Date(msg.timestamp).toLocaleTimeString('ar-EG', { hour: '2-digit', minute: '2-digit' });

          return `
            <div class="chat-msg ${isMe ? 'msg-ahmed' : 'msg-rody'} style-${bubbleStyle}" style="align-self: ${isMe ? 'flex-end' : 'flex-start'};">
              <div class="bubble-content">
                ${msg.type === 'image' ? `<img src="${msg.content}" class="chat-image-preview" alt="ØµÙˆØ±Ø©">` :
                  msg.type === 'voice' ? `<div class="chat-audio-player"><audio controls src="${msg.content}" style="height: 32px;"></audio></div>` :
                  msg.content}
                ${msg.decoration ? `<span style="position: absolute; top: -6px; ${isMe ? 'left' : 'right'}: -4px; font-size: 12px;">${msg.decoration}</span>` : ''}
              </div>
              <div class="msg-meta">
                <span>${time}</span>
                ${isMe ? `<span>${msg.isRead ? 'âœ“âœ“' : 'âœ“'}</span>` : ''}
              </div>
            </div>
          `;
        }).join('')}
      </div>

      <!-- Message Input -->
      <div class="glass-panel" style="padding: 10px; display: flex; align-items: center; gap: 8px; position: sticky; bottom: 72px;">
        <label for="chat-image-input" style="cursor: pointer; font-size: 20px; padding: 4px;">ðŸ“·</label>
        <input type="file" id="chat-image-input" accept="image/*" style="display: none;">
        <button id="chat-voice-btn" style="font-size: 20px; background: none; border: none; cursor: pointer; padding: 4px;">ðŸŽ¤</button>
        <input id="chat-text-input" type="text" placeholder="${this.t('Ø§ÙƒØªØ¨ Ø±Ø³Ø§Ù„Ø©...','Type a message...')}" style="
          flex: 1; padding: 10px 14px; border-radius: 14px; border: 1px solid var(--surface-glass-border);
          background: var(--surface-glass); color: var(--text-main); font-size: 14px; outline: none;
        ">
        <select id="chat-decoration-select" style="padding: 6px; border-radius: 10px; font-size: 14px; background: var(--surface-glass); border: 1px solid var(--surface-glass-border); color: var(--text-main);">
          <option value="">Ø¨Ø¯ÙˆÙ†</option>
          <option value="â¤ï¸">â¤ï¸</option>
          <option value="ðŸŒ¸">ðŸŒ¸</option>
          <option value="â­">â­</option>
          <option value="ðŸ±">ðŸ±</option>
          <option value="ðŸ¶">ðŸ¶</option>
        </select>
        <button id="chat-send-btn" class="glass-btn" style="padding: 10px 14px;">âž¤</button>
      </div>
    `;

    // Init Heart Counter
    const heartArea = document.getElementById('heart-counter-area');
    if (heartArea && s.chatCreatedAt) {
      if (this.heartCounter) this.heartCounter.destroy();
      this.heartCounter = new HeartCounter(s.chatCreatedAt, heartArea, (details) => {
        alert(`${this.t('Ù…Ø¯Ø© ØªÙˆØ§ØµÙ„Ù†Ø§','Our connection')}:\n${details.totalDays} ${this.t('ÙŠÙˆÙ…','days')}\n${details.months} ${this.t('Ø´Ù‡Ø±','months')} Ùˆ ${details.daysInMonth} ${this.t('ÙŠÙˆÙ…','days')}\n${details.hours}:${details.minutes}:${details.seconds}`);
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
          voiceBtn.textContent = 'ðŸŽ¤';
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
          voiceBtn.textContent = 'â¹ï¸';
        } catch (err) {
          alert(this.t('ÙŠØ±Ø¬Ù‰ Ø§Ù„Ø³Ù…Ø§Ø­ Ø¨Ø§Ø³ØªØ®Ø¯Ø§Ù… Ø§Ù„Ù…ÙŠÙƒØ±ÙˆÙÙˆÙ†','Please allow microphone access'));
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
      { id: 'default', name: this.t('ÙƒÙ„Ø§Ø³ÙŠÙƒÙŠ','Classic'), preview: 'ðŸ’Ž', cost: 0 },
      { id: 'flowers', name: this.t('Ø²Ù‡ÙˆØ±','Flowers'), preview: 'ðŸŒ¸', cost: 0 },
      { id: 'hearts', name: this.t('Ù‚Ù„ÙˆØ¨','Hearts'), preview: 'ðŸ’–', cost: 0 },
      { id: 'cats', name: this.t('Ù‚Ø·Ø·','Cats'), preview: 'ðŸ±', cost: 30 },
      { id: 'dogs', name: this.t('ÙƒÙ„Ø§Ø¨','Dogs'), preview: 'ðŸ¶', cost: 30 },
      { id: 'clouds', name: this.t('Ø³Ø­Ø§Ø¨','Clouds'), preview: 'â˜ï¸', cost: 50 },
      { id: 'stars', name: this.t('Ù†Ø¬ÙˆÙ…','Stars'), preview: 'âœ¨', cost: 50 }
    ];
    const currentStyle = realtimeStore.state.users[this.currentUser].bubbleStyle;
    const coins = realtimeStore.state.users[this.currentUser].coins;

    el.innerHTML = `
      <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px;">
        <h2 style="font-size: 20px; font-weight: 800;">ðŸŽ¨ ${this.t('Ø£Ù†Ù…Ø§Ø· Ø§Ù„ÙÙ‚Ø§Ø¹Ø§Øª','Bubble Styles')}</h2>
        <span class="glass-card" style="padding: 6px 14px; font-size: 13px; font-weight: 700;">ðŸª™ ${coins}</span>
      </div>

      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
        ${styles.map(s => {
          const isActive = s.id === currentStyle;
          const canAfford = coins >= s.cost || s.cost === 0;
          return `
            <div class="glass-card" style="padding: 18px; text-align: center; border: 2px solid ${isActive ? 'var(--primary)' : 'transparent'};">
              <div style="font-size: 36px; margin-bottom: 6px;">${s.preview}</div>
              <div style="font-weight: 700; font-size: 14px; margin-bottom: 4px;">${s.name}</div>
              <div style="font-size: 12px; color: var(--text-muted); margin-bottom: 8px;">${s.cost === 0 ? this.t('Ù…Ø¬Ø§Ù†ÙŠ','Free') : `ðŸª™ ${s.cost}`}</div>
              <button class="glass-btn bubble-select-btn" data-style="${s.id}" data-cost="${s.cost}" style="padding: 6px 14px; font-size: 12px; width: 100%; ${isActive ? 'opacity: 0.6;' : ''}">
                ${isActive ? this.t('Ù…ÙÙØ¹Ù‘Ù„','Active') : (canAfford ? this.t('Ø§Ø³ØªØ®Ø¯Ù…','Use') : this.t('ØºÙŠØ± ÙƒØ§ÙÙŠ','Not enough'))}
              </button>
            </div>
          `;
        }).join('')}
      </div>
      <button class="glass-btn-secondary" style="margin-top: 16px; width: 100%; padding: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); border-radius: 14px; background: var(--surface-glass); color: var(--text-main);" onclick="nexusApp.navigate('chat')">
        ${this.t('Ø±Ø¬ÙˆØ¹ Ù„Ù„Ø¯Ø±Ø¯Ø´Ø©','Back to Chat')} â†©ï¸
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
          <div style="font-size: 56px; margin-bottom: 16px;">ðŸŽ®</div>
          <h2 style="font-size: 20px; font-weight: 800; margin-bottom: 10px;">${this.t('Ø³Ø¬Ù„ Ø¯Ø®ÙˆÙ„ Ø£ÙˆÙ„Ø§Ù‹','Login first')}</h2>
          <p style="color: var(--text-muted); margin-bottom: 20px;">${this.t('Ù„Ø§Ø²Ù… ØªØ³Ø¬Ù„ Ø¯Ø®ÙˆÙ„ Ø§Ù„Ø¯Ø±Ø¯Ø´Ø© Ø§Ù„Ø£ÙˆÙ„ Ø¹Ø´Ø§Ù† ØªÙ„Ø¹Ø¨','Login to chat first to play')}</p>
          <button class="glass-btn" onclick="nexusApp.navigate('chat-login')">${this.t('ØªØ³Ø¬ÙŠÙ„ Ø¯Ø®ÙˆÙ„','Login')} ðŸ’¬</button>
        </div>
      `;
      return;
    }

    const coins = realtimeStore.state.users[this.currentUser].coins;
    el.innerHTML = `
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h2 style="font-size: 22px; font-weight: 800;">ðŸŽ® ${this.t('Ø§Ù„Ø£Ù„Ø¹Ø§Ø¨','Games')}</h2>
        <span class="glass-card" style="padding: 6px 14px; font-size: 13px; font-weight: 700;">ðŸª™ ${coins}</span>
      </div>

      <!-- Game Tabs -->
      <div style="display: flex; gap: 8px; margin-bottom: 16px; overflow-x: auto; padding-bottom: 4px;">
        <button class="glass-btn game-tab-btn active" data-game="xo" style="flex-shrink: 0;">â­• XO</button>
        <button class="glass-btn-secondary game-tab-btn" data-game="connect4" style="flex-shrink: 0; padding: 8px 14px; border-radius: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); background: var(--surface-glass); color: var(--text-main);">ðŸ”´ Connect 4</button>
        <button class="glass-btn-secondary game-tab-btn" data-game="racing" style="flex-shrink: 0; padding: 8px 14px; border-radius: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); background: var(--surface-glass); color: var(--text-main);">ðŸŽï¸ ${this.t('Ø³Ø¨Ø§Ù‚','Race')}</button>
        <button class="glass-btn-secondary game-tab-btn" data-game="quiz" style="flex-shrink: 0; padding: 8px 14px; border-radius: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); background: var(--surface-glass); color: var(--text-main);">â“ ${this.t('Ø£Ø³Ø¦Ù„Ø©','Quiz')}</button>
      </div>

      <div id="game-container"></div>

      <button class="glass-btn-secondary" style="margin-top: 16px; width: 100%; padding: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); border-radius: 14px; background: var(--surface-glass); color: var(--text-main);" onclick="nexusApp.navigate('entertainment')">
        ${this.t('Ø±Ø¬ÙˆØ¹','Back')} â†©ï¸
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
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">ðŸ•Œ ${this.t('Ø§Ù„Ù‚Ø³Ù… Ø§Ù„Ø¥Ø³Ù„Ø§Ù…ÙŠ','Islamic')}</h2>

      <!-- Prayer Times Full -->
      <div class="glass-card" style="padding: 18px; margin-bottom: 14px;">
        <h3 style="font-weight: 700; font-size: 16px; margin-bottom: 12px;">ðŸ• ${this.t('Ù…ÙˆØ§Ù‚ÙŠØª Ø§Ù„ØµÙ„Ø§Ø©','Prayer Times')}</h3>
        ${[
          { name: this.t('Ø§Ù„ÙØ¬Ø±','Fajr'), time: prayerTimes.fajr, emoji: 'ðŸŒ…' },
          { name: this.t('Ø§Ù„Ø´Ø±ÙˆÙ‚','Sunrise'), time: prayerTimes.sunrise, emoji: 'â˜€ï¸' },
          { name: this.t('Ø§Ù„Ø¸Ù‡Ø±','Dhuhr'), time: prayerTimes.dhuhr, emoji: 'ðŸŒž' },
          { name: this.t('Ø§Ù„Ø¹ØµØ±','Asr'), time: prayerTimes.asr, emoji: 'ðŸŒ¤ï¸' },
          { name: this.t('Ø§Ù„Ù…ØºØ±Ø¨','Maghrib'), time: prayerTimes.maghrib, emoji: 'ðŸŒ…' },
          { name: this.t('Ø§Ù„Ø¹Ø´Ø§Ø¡','Isha'), time: prayerTimes.isha, emoji: 'ðŸŒ™' }
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
          <div style="font-size: 32px; margin-bottom: 6px;">ðŸŒ…</div>
          <div style="font-weight: 700; font-size: 14px;">${this.t('Ø£Ø°ÙƒØ§Ø± Ø§Ù„ØµØ¨Ø§Ø­','Morning Azkar')}</div>
        </div>
        <div class="glass-card" style="padding: 20px; text-align: center; cursor: pointer;" onclick="nexusApp.currentAzkarType='evening'; nexusApp.navigate('azkar');">
          <div style="font-size: 32px; margin-bottom: 6px;">ðŸŒ™</div>
          <div style="font-weight: 700; font-size: 14px;">${this.t('Ø£Ø°ÙƒØ§Ø± Ø§Ù„Ù…Ø³Ø§Ø¡','Evening Azkar')}</div>
        </div>
        <div class="glass-card" style="padding: 20px; text-align: center; cursor: pointer; grid-column: span 2;" onclick="nexusApp.navigate('quran');">
          <div style="font-size: 32px; margin-bottom: 6px;">ðŸ“–</div>
          <div style="font-weight: 700; font-size: 14px;">${this.t('Ø§Ù„Ù‚Ø±Ø¢Ù† Ø§Ù„ÙƒØ±ÙŠÙ…','Holy Quran')}</div>
        </div>
      </div>

      <button class="glass-btn-secondary" style="margin-top: 16px; width: 100%; padding: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); border-radius: 14px; background: var(--surface-glass); color: var(--text-main);" onclick="nexusApp.navigate('entertainment')">
        ${this.t('Ø±Ø¬ÙˆØ¹','Back')} â†©ï¸
      </button>
    `;
  }

  // =========================================================
  // AZKAR VIEWER
  // =========================================================
  renderAzkar(el) {
    const type = this.currentAzkarType || 'morning';
    const azkar = AZKAR_DATA[type] || [];
    const title = type === 'morning' ? this.t('Ø£Ø°ÙƒØ§Ø± Ø§Ù„ØµØ¨Ø§Ø­','Morning Azkar') : this.t('Ø£Ø°ÙƒØ§Ø± Ø§Ù„Ù…Ø³Ø§Ø¡','Evening Azkar');

    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">${type === 'morning' ? 'ðŸŒ…' : 'ðŸŒ™'} ${title}</h2>
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
        ${this.t('Ø±Ø¬ÙˆØ¹','Back')} â†©ï¸
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
            btn.textContent = 'âœ“';
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
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">ðŸ“– ${this.t('Ø§Ù„Ù‚Ø±Ø¢Ù† Ø§Ù„ÙƒØ±ÙŠÙ…','Holy Quran')}</h2>
      <div class="glass-card" style="padding: 14px; margin-bottom: 14px;">
        <input id="quran-search" type="text" placeholder="${this.t('Ø§Ø¨Ø­Ø« Ø¹Ù† Ø³ÙˆØ±Ø©...','Search surah...')}" style="
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
              <div style="font-size: 12px; color: var(--text-muted);">${s.englishName} â€” ${s.verses} ${this.t('Ø¢ÙŠØ©','verses')} â€” ${s.type}</div>
            </div>
          </div>
        `).join('')}
      </div>
      <button class="glass-btn-secondary" style="margin-top: 10px; width: 100%; padding: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); border-radius: 14px; background: var(--surface-glass); color: var(--text-main);" onclick="nexusApp.navigate('islamic')">
        ${this.t('Ø±Ø¬ÙˆØ¹','Back')} â†©ï¸
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
            <div style="font-size: 24px;">â³</div>
            <div>${this.t('Ø¬Ø§Ø±ÙŠ ØªØ­Ù…ÙŠÙ„ Ø³ÙˆØ±Ø©','Loading Surah')} ${surah.name}...</div>
          </div>
        `;

        const verses = await fetchFullSurahText(id);
        if (!verses) {
          el.innerHTML = `<div class="glass-card" style="padding: 20px; text-align: center;">${this.t('ØªØ¹Ø°Ø± ØªØ­Ù…ÙŠÙ„ Ø§Ù„Ø³ÙˆØ±Ø©. ØªØ£ÙƒØ¯ Ù…Ù† Ø§Ù„Ø§ØªØµØ§Ù„ Ø¨Ø§Ù„Ø¥Ù†ØªØ±Ù†Øª.','Could not load surah. Check your connection.')}</div>
          <button class="glass-btn" style="margin-top: 12px;" onclick="nexusApp.navigate('quran')">${this.t('Ø±Ø¬ÙˆØ¹','Back')}</button>`;
          return;
        }

        el.innerHTML = `
          <div style="margin-bottom: 16px;">
            <h2 style="font-size: 22px; font-weight: 800; text-align: center;">${surah.name}</h2>
            <div style="text-align: center; font-size: 13px; color: var(--text-muted);">${surah.englishName} â€” ${surah.verses} ${this.t('Ø¢ÙŠØ©','verses')}</div>
          </div>

          <div class="glass-card" style="padding: 20px;">
            ${id !== 9 ? '<div style="text-align: center; font-size: 18px; font-weight: 600; margin-bottom: 16px; color: var(--primary);">Ø¨ÙØ³Ù’Ù…Ù Ø§Ù„Ù„ÙŽÙ‘Ù‡Ù Ø§Ù„Ø±ÙŽÙ‘Ø­Ù’Ù…ÙŽÙ°Ù†Ù Ø§Ù„Ø±ÙŽÙ‘Ø­ÙÙŠÙ…Ù</div>' : ''}
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
            ${this.t('Ø±Ø¬ÙˆØ¹ Ù„Ù‚Ø§Ø¦Ù…Ø© Ø§Ù„Ø³ÙˆØ±','Back to Surah list')} â†©ï¸
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
          <div style="font-size: 56px; margin-bottom: 16px;">ðŸ˜Š</div>
          <h2 style="font-size: 20px; font-weight: 800; margin-bottom: 10px;">${this.t('Ø³Ø¬Ù„ Ø¯Ø®ÙˆÙ„ Ø£ÙˆÙ„Ø§Ù‹','Login first')}</h2>
          <button class="glass-btn" onclick="nexusApp.navigate('chat-login')">${this.t('ØªØ³Ø¬ÙŠÙ„ Ø¯Ø®ÙˆÙ„','Login')} ðŸ’¬</button>
        </div>
      `;
      return;
    }

    const s = realtimeStore.state;
    const me = s.users[this.currentUser];
    const other = s.users[this.currentUser === 'ahmed' ? 'rody' : 'ahmed'];
    const moods = [
      { emoji: 'ðŸ˜Š', label: this.t('Ù…Ø¨Ø³ÙˆØ·','Happy') },
      { emoji: 'ðŸ˜¢', label: this.t('ØªØ¹Ø¨Ø§Ù†','Tired') },
      { emoji: 'ðŸ˜', label: this.t('Ø²Ù‡Ù‚Ø§Ù†','Bored') },
      { emoji: 'ðŸ˜”', label: this.t('Ù…Ø¶Ø§ÙŠÙ‚','Upset') },
      { emoji: 'ðŸ˜Œ', label: this.t('ÙƒÙˆÙŠØ³','Fine') },
      { emoji: 'ðŸ¥³', label: this.t('ÙØ±Ø­Ø§Ù†','Excited') },
      { emoji: 'ðŸ˜¤', label: this.t('Ù…Ù„Ø§Ù†','Frustrated') },
      { emoji: 'ðŸ˜‘', label: this.t('Ù…Ø´ Ø£Ø­Ø³Ù† Ø­Ø§Ø¬Ø©','Not great') }
    ];

    el.innerHTML = `
      <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 16px;">ðŸ˜Š ${this.t('Ù†ÙØ³ÙŠØªÙŠ','My Mood')}</h2>

      <!-- Other user mood -->
      <div class="glass-card" style="padding: 16px; margin-bottom: 16px;">
        <div style="font-weight: 700; font-size: 14px; margin-bottom: 6px;">${other.name} ${this.t('Ø­Ø§Ø³Ø³ Ø¨Ø¥ÙŠÙ‡','feels')}</div>
        <div style="font-size: 28px; margin-bottom: 4px;">${other.mood?.emoji || 'â€”'}</div>
        <div style="font-size: 13px; color: var(--text-muted);">${other.mood?.text || this.t('Ù„Ø³Ù‡ Ù…Ø§ Ø­Ø¯Ø¯ Ø­Ø§Ù„ØªÙ‡','No mood set')}</div>
      </div>

      <!-- My mood selector -->
      <div class="glass-card" style="padding: 16px; margin-bottom: 14px;">
        <div style="font-weight: 700; font-size: 14px; margin-bottom: 10px;">${this.t('Ø­Ø§Ù„ØªÙƒ Ø¯Ù„ÙˆÙ‚ØªÙŠ','Your mood now')}</div>
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
        <input id="mood-custom-text" type="text" placeholder="${this.t('Ø§ÙƒØªØ¨ Ø¬Ù…Ù„Ø© Ù…Ø®ØµØµØ©...','Custom text...')}" value="${me.mood?.text || ''}" style="
          width: 100%; padding: 10px 14px; border-radius: 12px; border: 1px solid var(--surface-glass-border);
          background: var(--surface-glass); color: var(--text-main); font-size: 14px; outline: none;
        ">
      </div>

      <button class="glass-btn-secondary" style="width: 100%; padding: 12px; cursor: pointer; border: 1px solid var(--surface-glass-border); border-radius: 14px; background: var(--surface-glass); color: var(--text-main);" onclick="nexusApp.navigate('entertainment')">
        ${this.t('Ø±Ø¬ÙˆØ¹','Back')} â†©ï¸
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
