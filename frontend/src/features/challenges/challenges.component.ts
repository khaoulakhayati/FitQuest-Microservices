import { Component, OnInit, inject, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ChallengeService } from '../../core/services/challenge.service';
import { AuthService } from '../../core/services/auth.service';
import { Challenge } from '../../core/models';

@Component({
  selector: 'fq-challenges',
  standalone: true,
  imports: [MatIconModule, MatProgressBarModule],
  template: `
    <header class="mb-8">
      <h1 class="page-title">Challenges</h1>
      <p class="text-sm text-[var(--fq-muted)]">Track challenge progress and rewards</p>
    </header>

    <section class="grid gap-4 lg:grid-cols-2">
      @for (c of challenges(); track c.id) {
        <article class="glass-card p-5">
          <section class="mb-3 flex items-start justify-between">
            <section>
              <h3 class="text-lg font-semibold text-[var(--fq-text)]">{{ c.title }}</h3>
              <p class="text-sm text-[var(--fq-muted)]">{{ c.description }}</p>
            </section>
            <span
              class="rounded-full px-3 py-1 text-xs font-medium"
              [class]="statusClass(c.status)"
            >
              {{ c.status }}
            </span>
          </section>
          <mat-progress-bar mode="determinate" [value]="progressPct(c)" class="!h-2 !rounded-full" />
          <section class="mt-2 flex justify-between text-sm">
            <span class="text-[var(--fq-muted)]">{{ progressValue(c) }} / {{ targetValue(c) }}</span>
            <span class="font-medium text-[var(--fq-accent)]">+{{ rewardValue(c) }} XP</span>
          </section>
          <button type="button" class="mt-4 rounded-md border border-[var(--fq-border)] px-4 py-2 text-sm font-medium text-[var(--fq-accent)]" (click)="join(c)">
            Join challenge
          </button>
        </article>
      } @empty {
        <section class="glass-card p-6 text-sm text-[var(--fq-muted)]">
          No challenges from the backend yet.
        </section>
      }
    </section>
  `,
})
export class ChallengesComponent implements OnInit {
  private readonly challengeService = inject(ChallengeService);
  private readonly auth = inject(AuthService);
  readonly challenges = signal<Challenge[]>([]);

  ngOnInit(): void {
    this.challengeService.getAll().subscribe((list) => {
      this.challenges.set(Array.isArray(list) ? list : []);
    });
  }

  progressPct(c: Challenge): number {
    return Math.min(100, (this.progressValue(c) / this.targetValue(c)) * 100);
  }

  progressValue(c: Challenge): number {
    return c.progress ?? 0;
  }

  targetValue(c: Challenge): number {
    return c.target ?? c.goalPoints ?? 1;
  }

  rewardValue(c: Challenge): number {
    return c.xpReward ?? c.goalPoints ?? 0;
  }

  join(c: Challenge): void {
    const user = this.auth.currentUser();
    if (!user) return;
    this.challengeService.join(c.id, user.id, user.displayName || user.username).subscribe();
  }

  statusClass(status: string): string {
    const normalized = status.toLowerCase();
    const map: Record<string, string> = {
      active: 'bg-blue-50 text-blue-700',
      draft: 'bg-slate-100 text-slate-600',
      completed: 'bg-green-50 text-green-700',
      locked: 'bg-slate-500/20 text-slate-400',
    };
    return map[normalized] ?? map['active'];
  }
}
