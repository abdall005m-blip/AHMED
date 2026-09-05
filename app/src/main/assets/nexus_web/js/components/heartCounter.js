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
            ❤️
          </div>
          <div>
            <div style="font-size: 13px; color: #9f1239; font-weight: 600;">
              مدة تواصلنا بالحب والأيام
            </div>
            <div style="font-size: 20px; font-weight: 800; color: #881337;">
              <span id="heart-days-num">${time.totalDays}</span> يوم مكتمل
            </div>
          </div>
        </div>
        <div style="display: flex; align-items: center; gap: 8px;">
          <span style="font-size: 12px; color: #be123c; font-weight: 600; background: rgba(255,255,255,0.7); padding: 4px 10px; border-radius: 20px;">
            اضغط للتفاصيل ✨
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
