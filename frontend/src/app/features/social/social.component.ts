import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/services/auth.service';
import { SocialService } from '../../core/services/social.service';
import { ProfileService } from '../../core/services/profile.service';
import { FitnessGroup, SocialPost, User } from '../../core/models';

@Component({
  selector: 'fq-social',
  standalone: true,
  imports: [ReactiveFormsModule, MatIconModule, DatePipe],
  template: `
    <header class="mb-8 grid gap-4 rounded-lg border border-[var(--fq-border)] bg-[var(--fq-surface)] p-5 lg:grid-cols-[1fr_18rem]">
      <section>
        <p class="eyebrow">Groups</p>
        <h1 class="page-title">Team Feed</h1>
        <p class="mt-1 text-sm text-[var(--fq-muted)]">
          Posts and reactions are visible only inside your training group.
        </p>
      </section>
      <img
        src="https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?auto=format&fit=crop&w=900&q=80"
        alt="Coach helping athlete train"
        class="h-32 w-full rounded-md object-cover"
      />
    </header>

    <section class="grid gap-6 xl:grid-cols-[20rem_1fr]">
      <aside class="space-y-4">
        <section class="glass-card p-4">
          <h2 class="mb-3 text-base font-semibold text-[var(--fq-text)]">My Groups</h2>
          <section class="space-y-2">
            @for (group of groups(); track group.id) {
              <button
                type="button"
                class="w-full rounded-md border border-[var(--fq-border)] px-3 py-2 text-left text-sm transition"
                [class.border-blue-500]="selectedGroupId() === group.id"
                [class.bg-blue-50]="selectedGroupId() === group.id"
                (click)="selectedGroupId.set(group.id)"
              >
                <span class="block font-medium text-[var(--fq-text)]">{{ group.name }}</span>
                <span class="text-xs text-[var(--fq-muted)]">{{ group.memberCount }} members</span>
              </button>
            } @empty {
              <p class="text-sm text-[var(--fq-muted)]">No groups from the backend yet.</p>
            }
          </section>
        </section>

        @if (selectedGroup(); as group) {
          <section class="glass-card p-4">
            <h2 class="mb-2 text-base font-semibold text-[var(--fq-text)]">Weekly Plan</h2>
            <p class="text-sm text-[var(--fq-muted)]">{{ group.weeklyWorkoutPlan || 'No plan posted yet.' }}</p>
          </section>
        }

        @if (isCoach()) {
          <section class="glass-card p-4">
            <h2 class="mb-3 text-base font-semibold text-[var(--fq-text)]">Coach Tools</h2>
            <form [formGroup]="groupForm" (ngSubmit)="createGroup()" class="space-y-2">
              <input class="field" formControlName="name" placeholder="Group name" />
              <textarea class="field min-h-20" formControlName="weeklyWorkoutPlan" placeholder="Weekly workout plan"></textarea>
              <button class="btn-primary w-full" type="submit">Create group</button>
            </form>

            <form [formGroup]="memberForm" (ngSubmit)="addMember()" class="mt-4 space-y-2">
              <select class="field" formControlName="userId">
                <option [ngValue]="0">Select member</option>
                @for (user of availableUsers(); track user.id) {
                  <option [ngValue]="user.id">{{ displayName(user) }}</option>
                }
              </select>
              <button class="w-full rounded-md border border-[var(--fq-border)] px-4 py-2 text-sm font-medium text-[var(--fq-accent)]" type="submit">
                Add to selected group
              </button>
            </form>
          </section>
        }
      </aside>

      <section>
        <form [formGroup]="postForm" (ngSubmit)="publish()" class="glass-card mb-6 p-4">
          <textarea
            formControlName="content"
            rows="3"
            placeholder="Share an update with your group..."
            class="field min-h-24 resize-none"
          ></textarea>
          <button type="submit" class="btn-primary mt-3" [disabled]="!selectedGroupId()">Post to group</button>
        </form>

        <section class="space-y-4">
          @for (post of posts(); track post.id) {
            <article class="glass-card p-5">
              <header class="mb-3 flex items-center gap-3">
                <span class="flex h-10 w-10 items-center justify-center rounded-full bg-[var(--fq-accent-soft)] text-sm font-bold text-[var(--fq-accent)]">
                  {{ post.authorUsername.slice(0, 1).toUpperCase() }}
                </span>
                <section>
                  <p class="font-medium text-[var(--fq-text)]">{{ post.authorUsername }}</p>
                  <p class="text-xs text-[var(--fq-muted)]">{{ post.createdAt | date: 'short' }}</p>
                </section>
              </header>
              <p class="text-sm leading-6 text-[var(--fq-text)]">{{ post.content }}</p>
              <section class="mt-4 flex gap-2">
                <button type="button" class="vote-btn" [class.vote-active]="post.myVote === 'UP'" (click)="vote(post, 'up')">
                  <mat-icon class="!h-4 !w-4 !text-[18px]">arrow_upward</mat-icon>
                  {{ post.upvotes }}
                </button>
                <button type="button" class="vote-btn" [class.vote-active]="post.myVote === 'DOWN'" (click)="vote(post, 'down')">
                  <mat-icon class="!h-4 !w-4 !text-[18px]">arrow_downward</mat-icon>
                  {{ post.downvotes }}
                </button>
              </section>
            </article>
          } @empty {
            <section class="glass-card p-6 text-center text-sm text-[var(--fq-muted)]">
              No group posts yet.
            </section>
          }
        </section>
      </section>
    </section>
  `,
  styles: `
    .field {
      width: 100%;
      border-radius: 0.375rem;
      border: 1px solid var(--fq-border);
      background: var(--fq-surface);
      padding: 0.625rem 0.75rem;
      color: var(--fq-text);
      outline: none;
    }
    .field:focus {
      border-color: var(--fq-accent);
    }
    .vote-btn {
      display: inline-flex;
      align-items: center;
      gap: 0.25rem;
      border: 1px solid var(--fq-border);
      border-radius: 0.375rem;
      padding: 0.375rem 0.625rem;
      color: var(--fq-muted);
      font-size: 0.875rem;
    }
    .vote-active {
      border-color: var(--fq-accent);
      color: var(--fq-accent);
      background: var(--fq-accent-soft);
    }
  `,
})
export class SocialComponent implements OnInit {
  private readonly social = inject(SocialService);
  private readonly auth = inject(AuthService);
  private readonly profile = inject(ProfileService);
  private readonly fb = inject(FormBuilder);

  readonly groups = signal<FitnessGroup[]>([]);
  readonly posts = signal<SocialPost[]>([]);
  readonly users = signal<User[]>([]);
  readonly selectedGroupId = signal<string | null>(null);

  readonly postForm = this.fb.nonNullable.group({ content: ['', Validators.required] });
  readonly groupForm = this.fb.nonNullable.group({ name: ['', Validators.required], weeklyWorkoutPlan: [''] });
  readonly memberForm = this.fb.nonNullable.group({ userId: [0, Validators.required] });

  ngOnInit(): void {
    this.load();
    if (this.isCoach()) {
      this.loadUsers();
    }
  }

  selectedGroup(): FitnessGroup | undefined {
    return this.groups().find((group) => group.id === this.selectedGroupId());
  }

  isCoach(): boolean {
    const roles = this.auth.currentUser()?.roles ?? [];
    return roles.includes('ROLE_COACH') || roles.includes('ROLE_ADMIN');
  }

  availableUsers(): User[] {
    const currentUserId = this.auth.currentUser()?.id;
    return this.users().filter((user) => user.id !== currentUserId);
  }

  displayName(user: User): string {
    const name = this.memberName(user);
    return `${name} (${user.email})`;
  }

  publish(): void {
    const groupId = this.selectedGroupId();
    const content = this.postForm.value.content?.trim();
    if (!groupId || !content) return;
    this.social.createPost(groupId, content).subscribe(() => {
      this.postForm.reset();
      this.loadPosts();
    });
  }

  createGroup(): void {
    const name = this.groupForm.value.name?.trim();
    if (!name) return;
    this.social.createGroup({
      name,
      weeklyWorkoutPlan: this.groupForm.value.weeklyWorkoutPlan ?? '',
    }).subscribe(() => {
      this.groupForm.reset();
      this.loadGroups();
    });
  }

  addMember(): void {
    const groupId = this.selectedGroupId();
    const userId = Number(this.memberForm.value.userId);
    if (!groupId || !userId) return;
    const user = this.users().find((item) => item.id === userId);
    this.social.addMember(groupId, userId, user ? this.memberName(user) : '').subscribe(() => {
      this.memberForm.reset({ userId: 0 });
      this.loadGroups();
    });
  }

  vote(post: SocialPost, direction: 'up' | 'down'): void {
    const request = direction === 'up' ? this.social.upvote(post.id) : this.social.downvote(post.id);
    request.subscribe((updated) => {
      if (!updated) return;
      this.posts.update((posts) => posts.map((item) => (item.id === updated.id ? updated : item)));
    });
  }

  private load(): void {
    this.loadGroups();
    this.loadPosts();
  }

  private loadGroups(): void {
    this.social.getGroups().subscribe((list) => {
      const groups = Array.isArray(list) ? list : [];
      this.groups.set(groups);
      if (!this.selectedGroupId() && groups.length) {
        this.selectedGroupId.set(groups[0].id);
      } else if (!groups.length) {
        this.selectedGroupId.set(null);
      }
    });
  }

  private loadPosts(): void {
    this.social.getFeed().subscribe((list) => {
      this.posts.set(Array.isArray(list) ? list : []);
    });
  }

  private loadUsers(): void {
    this.profile.getUsers().subscribe((list) => {
      this.users.set(Array.isArray(list) ? list : []);
    });
  }

  private memberName(user: User): string {
    return user.displayName ?? user.profile?.displayName ?? user.username;
  }
}
