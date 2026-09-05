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
        ? (winner === 'draw' ? 'تعادل رائع بينكما! 🤝' : `الفائز: ${winner === 'ahmed' ? 'Ahmed 🏆' : 'Rody 🏆'} (+20 كوينز)`)
        : `الدور الحالي: ${turn === 'ahmed' ? 'Ahmed (X)' : 'Rody (O)'} ${isMyTurn ? '👉 دورك الآن!' : '⏳ انتظر...'}`;

      container.innerHTML = `
        <div class="glass-card" style="padding: 20px; text-align: center;">
          <div style="font-size: 18px; font-weight: 700; margin-bottom: 6px;">لعبة إكس أو (XO) ⚔️</div>
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
            إعادة بدء اللعبة 🔄
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
  // GAME 2: CONNECT FOUR (4 على التوالي)
  // -------------------------------------------------------------
  initConnectFour(container) {
    const render = () => {
      const session = realtimeStore.state.gameSessions.connect4;
      const isMyTurn = session.turn === this.currentUser && !session.winner;
      const status = session.winner
        ? `الفائز: ${session.winner === 'ahmed' ? 'Ahmed 🔴' : 'Rody 🟡'} (+25 كوينز)`
        : `الدور: ${session.turn === 'ahmed' ? 'Ahmed' : 'Rody'} ${isMyTurn ? '👉 دورك إسقاط القرص!' : '⏳ انتظر...'}`;

      container.innerHTML = `
        <div class="glass-card" style="padding: 20px; text-align: center;">
          <div style="font-size: 18px; font-weight: 700; margin-bottom: 6px;">Connect Four 🔴🟡</div>
          <div style="font-size: 14px; color: var(--primary); font-weight: 600; margin-bottom: 14px;">${status}</div>

          <!-- Drop Column Buttons -->
          <div style="display: grid; grid-template-columns: repeat(7, 38px); gap: 6px; justify-content: center; margin-bottom: 6px;">
            ${[0,1,2,3,4,5,6].map(col => `
              <button class="c4-drop-btn glass-btn" data-col="${col}" style="
                padding: 4px; font-size: 12px; height: 32px; border-radius: 8px;
                opacity: ${isMyTurn ? '1' : '0.4'};
              " ${!isMyTurn ? 'disabled' : ''}>⬇️</button>
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
            <button id="c4-restart" class="glass-btn" style="padding: 8px 18px; font-size: 13px;">إعادة اللعبة 🔄</button>
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
  // GAME 3: CAR RACING (سباق السيارات الخفيف)
  // -------------------------------------------------------------
  initRacing(container) {
    const render = () => {
      const session = realtimeStore.state.gameSessions.racing || { ahmedPos: 0, rodyPos: 0, winner: null };
      const status = session.winner
        ? `الفائز بالسباق: ${session.winner === 'ahmed' ? 'Ahmed 🏎️' : 'Rody 🏎️'} (+30 كوينز)`
        : 'اضغط بأسرع ما يمكنك على زر السرعة للوصول لخط النهاية! 🏁';

      container.innerHTML = `
        <div class="glass-card" style="padding: 20px; text-align: center;">
          <div style="font-size: 18px; font-weight: 700; margin-bottom: 6px;">سباق السيارات السريع 🏎️🏁</div>
          <div style="font-size: 13px; color: var(--text-muted); margin-bottom: 16px;">${status}</div>

          <!-- Track -->
          <div style="background: rgba(0,0,0,0.06); border-radius: 14px; padding: 14px; margin-bottom: 20px;">
            <!-- Ahmed Lane -->
            <div style="margin-bottom: 14px; text-align: right;">
              <span style="font-weight: 600; font-size: 13px;">Ahmed 🏎️ (أزرق)</span>
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
              <span style="font-weight: 600; font-size: 13px;">Rody 🏎️ (وردي)</span>
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
              دوس بنزين! 🚀💨
            </button>
            <button id="racing-reset-btn" class="glass-btn-secondary glass-btn" style="padding: 12px 20px;">
              سباق جديد 🔄
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
  // GAME 4: QUESTIONS TRIVIA (مسابقة الأسئلة)
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
              🏷️ ${q.category}
            </span>
            <div style="font-size: 13px; font-weight: 700; color: var(--primary);">
              النقاط: Ahmed (${session.scores.ahmed}) vs Rody (${session.scores.rody})
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
              ? `إجابة Ahmed: ${q.options[session.ahmedAnswer] || 'لم يجب'} | إجابة Rody: ${q.options[session.rodyAnswer] || 'لم يجب'}`
              : (hasMyAnswer ? 'تم تسجيل إجابتك السرية بنجاح 🔒 في انتظار الطرف الآخر...' : 'اختر إجابتك الآن!')
            }
          </div>

          <div style="display: flex; gap: 10px; justify-content: center;">
            <button id="quiz-reveal-btn" class="glass-btn" style="font-size: 13px; padding: 8px 18px;" ${session.revealed ? 'disabled' : ''}>
              كشف الإجابات 👁️
            </button>
            <button id="quiz-next-btn" class="glass-btn-secondary glass-btn" style="font-size: 13px; padding: 8px 18px;">
              السؤال التالي ➡️
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
