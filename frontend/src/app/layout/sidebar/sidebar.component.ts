import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';

interface NavItem {
  path: string;
  label: string;
  icon: string;
}

@Component({
  selector: 'fq-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, MatIconModule, MatButtonModule, MatTooltipModule],
  template: `
    <aside
      class="sticky top-0 flex h-screen w-64 shrink-0 flex-col border-r border-[var(--fq-border)] bg-[var(--fq-shell)] px-4 py-5"
    >
      <div class="mb-8 flex items-center gap-3 px-2">
        <span
          class="flex h-10 w-10 items-center justify-center rounded-lg bg-[var(--fq-accent)] text-sm font-bold text-white"
          >FQ</span
        >
        <div>
          <h1 class="text-lg font-semibold text-[var(--fq-text)]">FitQuest</h1>
          <p class="text-xs text-[var(--fq-muted)]">Fitness workspace</p>
        </div>
      </div>

      <nav class="flex flex-1 flex-col gap-1">
        @for (item of navItems; track item.path) {
          <a
            [routerLink]="item.path"
            routerLinkActive="nav-active"
            class="nav-link flex items-center gap-3 rounded-md px-3 py-2.5 text-sm font-medium transition-colors"
          >
            <mat-icon class="!h-5 !w-5 !text-[20px]">{{ item.icon }}</mat-icon>
            {{ item.label }}
          </a>
        }
      </nav>

      <div class="mt-auto space-y-3 border-t border-[var(--fq-border)] pt-4">
        <button
          mat-icon-button
          type="button"
          (click)="theme.toggle()"
          matTooltip="Toggle theme"
          class="!text-[var(--fq-text)]"
        >
          <mat-icon>{{ theme.isDark() ? 'light_mode' : 'dark_mode' }}</mat-icon>
        </button>
        @if (auth.currentUser(); as user) {
          <div class="rounded-lg border border-[var(--fq-border)] bg-[var(--fq-surface-soft)] px-3 py-2 text-sm">
            <p class="font-medium text-[var(--fq-text)]">{{ user.displayName || user.username }}</p>
            <p class="text-xs text-[var(--fq-muted)]">Lv. {{ user.level ?? 1 }}</p>
          </div>
        }
        <button
          type="button"
          class="flex w-full items-center gap-2 rounded-md px-3 py-2 text-sm text-[var(--fq-danger)] hover:bg-red-50"
          (click)="auth.logout()"
        >
          <mat-icon class="!h-5 !w-5">logout</mat-icon>
          Logout
        </button>
      </div>
    </aside>
  `,
  styles: `
    .nav-link {
      color: var(--fq-muted);
    }
    .nav-link:hover {
      color: var(--fq-text);
      background: var(--fq-surface-soft);
    }
    .nav-active {
      color: var(--fq-accent) !important;
      background: var(--fq-accent-soft) !important;
      box-shadow: inset 3px 0 0 var(--fq-accent);
    }
  `,
})
export class SidebarComponent {
  readonly auth = inject(AuthService);
  readonly theme = inject(ThemeService);

  readonly navItems: NavItem[] = [
    { path: '/dashboard', label: 'Dashboard', icon: 'dashboard' },
    { path: '/workouts', label: 'Workouts', icon: 'fitness_center' },
    { path: '/nutrition', label: 'Nutrition', icon: 'restaurant' },
    { path: '/challenges', label: 'Challenges', icon: 'emoji_events' },
    { path: '/leaderboard', label: 'Leaderboard', icon: 'leaderboard' },
    { path: '/social', label: 'Social Feed', icon: 'forum' },
    { path: '/notifications', label: 'Notifications', icon: 'notifications' },
    { path: '/ai-coach', label: 'AI Coach', icon: 'smart_toy' },
    { path: '/profile', label: 'Profile', icon: 'person' },
  ];
}
