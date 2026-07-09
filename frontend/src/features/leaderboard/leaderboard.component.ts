import { Component, OnInit, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { LeaderboardService } from '../../core/services/leaderboard.service';
import { LeaderboardEntry } from '../../core/models';

@Component({
  selector: 'fq-leaderboard',
  standalone: true,
  imports: [MatIconModule, DecimalPipe],
  template: `
    <header class="mb-8">
      <h1 class="page-title">Leaderboard</h1>
      <p class="text-sm text-[var(--fq-muted)]">Global rankings - climb to the top</p>
    </header>

    <section class="glass-card overflow-hidden">
      <table class="w-full text-left">
        <thead class="border-b border-[var(--fq-border)] text-sm text-[var(--fq-muted)]">
          <tr>
            <th class="p-4">Rank</th>
            <th class="p-4">Player</th>
            <th class="p-4">Level</th>
            <th class="p-4 text-right">XP</th>
          </tr>
        </thead>
        <tbody>
          @for (entry of entries(); track entry.userId) {
            <tr class="border-b border-[var(--fq-border)]/50 transition hover:bg-[var(--fq-surface-soft)]">
              <td class="p-4">
                @if (entry.rank <= 3) {
                  <mat-icon [class]="rankIconColor(entry.rank)">emoji_events</mat-icon>
                } @else {
                  <span class="font-medium text-[var(--fq-muted)]">#{{ entry.rank }}</span>
                }
              </td>
              <td class="p-4 font-medium text-[var(--fq-text)]">{{ playerName(entry) }}</td>
              <td class="p-4 text-[var(--fq-muted)]">Lv. {{ entry.level }}</td>
              <td class="p-4 text-right font-semibold text-[var(--fq-accent)]">
                {{ xpValue(entry) | number }}
              </td>
            </tr>
          }
        </tbody>
      </table>
      @if (entries().length === 0) {
        <section class="p-6 text-sm text-[var(--fq-muted)]">
          No leaderboard data from the backend yet.
        </section>
      }
    </section>
  `,
})
export class LeaderboardComponent implements OnInit {
  private readonly leaderboard = inject(LeaderboardService);
  readonly entries = signal<LeaderboardEntry[]>([]);

  ngOnInit(): void {
    this.leaderboard.getGlobal().subscribe((list) => {
      this.entries.set(Array.isArray(list) ? list : []);
    });
  }

  rankIconColor(rank: number): string {
    if (rank === 1) return '!text-yellow-400';
    if (rank === 2) return '!text-slate-300';
    return '!text-amber-600';
  }

  playerName(entry: LeaderboardEntry): string {
    return entry.username || `User ${entry.userId}`;
  }

  xpValue(entry: LeaderboardEntry): number {
    return entry.xp ?? entry.totalXp ?? 0;
  }
}
