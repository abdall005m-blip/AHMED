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
    const ampm = hours >= 12 ? 'م' : 'ص';
    let h12 = hours % 12 || 12;
    return `${h12.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')} ${ampm}`;
  }
}
