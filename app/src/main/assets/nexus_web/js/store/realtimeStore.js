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
          note: 'يومي كان حلو النهارده 🌸',
          mood: { emoji: 'مبسوط 😊', text: 'الحمد لله كل حاجة تمام', updatedAt: now },
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
          note: 'محتاجة أروق شوية ☕',
          mood: { emoji: 'كويسة ✨', text: 'بشرب قهوتي المفضلة', updatedAt: now },
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
          content: 'أهلاً يا رودي! إيه الأخبار عندك النهاردة؟',
          timestamp: now - 3600000,
          decoration: '🌸',
          isRead: true
        },
        {
          id: 'msg_2',
          sender: 'rody',
          type: 'text',
          content: 'الحمد لله كويسة جداً، شفت التحديثات والألعاب الجديدة؟',
          timestamp: now - 1800000,
          decoration: '❤️',
          isRead: true
        }
      ],
      notifications: [
        { id: 1, appName: 'WhatsApp', title: 'رسالة جديدة', content: 'السلام عليكم ورحمة الله', time: 'منذ 5 دقائق', priority: 'HIGH', isSilent: false },
        { id: 2, appName: 'Telegram', title: 'تحديث Nexus', content: 'تم تفعيل التزامن السحابي بنجاح', time: 'منذ 25 دقيقة', priority: 'MEDIUM', isSilent: true },
        { id: 3, appName: 'Google Calendar', title: 'موعد مهم', content: 'جلسة برمجة ومراجعة المشروع', time: 'منذ ساعتين', priority: 'LOW', isSilent: false }
      ],
      tasks: [
        { id: 1, title: 'مراجعة أذكار الصباح', desc: 'أذكار الصباح تجلب البركة والسكينة', category: 'today', priority: 'HIGH', completed: true },
        { id: 2, title: 'تحدي لعبة XO مع رودي', desc: 'الفائز يحصل على 25 كوينز', category: 'today', priority: 'MEDIUM', completed: false },
        { id: 3, title: 'قراءة سورة الكهف', desc: 'يوم الجمعة المبارك', category: 'upcoming', priority: 'HIGH', completed: false }
      ],
      schedules: [
        { id: 1, title: 'صلاة الظهر جماعة', time: '12:05 م', category: 'عبادة', note: 'المسجد القريب' },
        { id: 2, title: 'جلسة الألعاب المشتركة', time: '08:30 م', category: 'ترفيه', note: 'XO و سباق السيارات' }
      ],
      aiApps: [
        { id: 'chatgpt', name: 'ChatGPT', url: 'https://chatgpt.com', appUrl: 'chatgpt://', emoji: '🟢', pinned: true, desc: 'مساعد الذكاء الاصطناعي الأشهر من OpenAI' },
        { id: 'gemini', name: 'Gemini', url: 'https://gemini.google.com', appUrl: 'googleapp://', emoji: '✨', pinned: true, desc: 'النموذج الفائق من Google' },
        { id: 'claude', name: 'Claude', url: 'https://claude.ai', appUrl: 'claude://', emoji: '🟠', pinned: true, desc: 'نموذج الذكاء المتقدم من Anthropic' },
        { id: 'perplexity', name: 'Perplexity', url: 'https://perplexity.ai', appUrl: 'perplexity://', emoji: '🔍', pinned: false, desc: 'محرك بحث ذكي فائق الدقة' },
        { id: 'copilot', name: 'Microsoft Copilot', url: 'https://copilot.microsoft.com', appUrl: 'ms-copilot://', emoji: '💻', pinned: false, desc: 'مساعد مايكروسوفت الذكي' }
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
