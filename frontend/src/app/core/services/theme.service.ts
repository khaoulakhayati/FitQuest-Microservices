import { Injectable, signal } from '@angular/core';

const THEME_KEY = 'fitquest_theme';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  readonly isDark = signal(this.loadStored());

  constructor() {
    this.apply(this.isDark());
  }

  toggle(): void {
    this.isDark.update((v) => !v);
    this.apply(this.isDark());
  }

  private apply(dark: boolean): void {
    document.documentElement.classList.toggle('light', !dark);
    document.documentElement.classList.toggle('dark', dark);
    localStorage.setItem(THEME_KEY, dark ? 'dark' : 'light');
  }

  private loadStored(): boolean {
    return localStorage.getItem(THEME_KEY) === 'dark';
  }
}
