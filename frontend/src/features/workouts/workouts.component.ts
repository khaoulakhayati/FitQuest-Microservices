import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { WorkoutService } from '../../core/services/workout.service';
import { Exercise, Workout } from '../../core/models';
import { AuthService } from '../../core/services/auth.service';

interface WorkoutGroup {
  name: string;
  workouts: Workout[];
}

const MUSCLE_GROUP_ORDER = [
  'Chest',
  'Back',
  'Shoulders',
  'Biceps',
  'Triceps',
  'Legs',
  'Glutes',
  'Abs',
  'Cardio',
  'Full Body',
];

@Component({
  selector: 'fq-workouts',
  standalone: true,
  imports: [ReactiveFormsModule, MatIconModule, MatButtonModule],
  template: `
    <header class="mb-8 grid gap-4 rounded-lg border border-[var(--fq-border)] bg-[var(--fq-surface)] p-5 lg:grid-cols-[1fr_18rem]">
      <section>
        <p class="eyebrow">Training</p>
        <h1 class="page-title">Workout Library</h1>
        <p class="mt-1 text-sm text-[var(--fq-muted)]">Workouts are organized by muscle group. Select one, then record sets, reps, and weight.</p>
      </section>
      <img
        src="https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=900&q=80"
        alt="Gym training equipment"
        class="h-32 w-full rounded-md object-cover"
      />
    </header>

    <section class="grid gap-6 xl:grid-cols-[1fr_22rem]">
      <section class="space-y-6">
        @if (error()) {
          <section class="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
            {{ error() }}
          </section>
        }
        @if (success()) {
          <section class="rounded-lg border border-green-200 bg-green-50 px-4 py-3 text-sm font-medium text-green-700">
            {{ success() }}
          </section>
        }
        @for (group of groupedWorkouts(); track group.name) {
          <section class="glass-card p-5">
            <header class="mb-4 flex items-center justify-between">
              <h2 class="text-lg font-semibold text-[var(--fq-text)]">{{ group.name }}</h2>
              <span class="rounded-full bg-[var(--fq-accent-soft)] px-3 py-1 text-xs font-semibold text-[var(--fq-accent)]">
                {{ group.workouts.length }} workouts
              </span>
            </header>
            <section class="grid gap-3 md:grid-cols-2 2xl:grid-cols-3">
              @for (w of group.workouts; track w.id) {
                <article
                  class="cursor-pointer rounded-lg border border-[var(--fq-border)] bg-[var(--fq-surface)] p-4 transition hover:border-blue-300 hover:bg-[var(--fq-surface-soft)]"
                  [class.border-blue-500]="selectedWorkout()?.id === w.id"
                  [class.bg-blue-50]="selectedWorkout()?.id === w.id"
                  (click)="select(w)"
                >
                  <section class="mb-3 flex items-center gap-3">
                    <span class="flex h-9 w-9 items-center justify-center rounded-md bg-[var(--fq-accent-soft)] text-[var(--fq-accent)]">
                      <mat-icon class="!h-5 !w-5 !text-[20px]">{{ iconFor(group.name) }}</mat-icon>
                    </span>
                    <section>
                      <h3 class="font-semibold text-[var(--fq-text)]">{{ w.name }}</h3>
                      <p class="text-xs text-[var(--fq-muted)]">{{ w.difficulty || w.type || 'Workout' }}</p>
                    </section>
                  </section>
                  <p class="text-sm text-[var(--fq-muted)]">
                    {{ w.estimatedDurationMinutes || w.durationMinutes || 30 }} min
                    @if (exerciseNames(w)) {
                      - {{ exerciseNames(w) }}
                    }
                  </p>
                  <section class="mt-4 flex gap-2">
                    @if (canManageWorkout(w)) {
                      <button type="button" class="mini-btn" (click)="editWorkout(w); $event.stopPropagation()">Edit</button>
                      <button type="button" class="mini-btn danger" (click)="deleteWorkout(w); $event.stopPropagation()">Delete</button>
                    }
                  </section>
                </article>
              }
            </section>
          </section>
        } @empty {
          <section class="glass-card p-6 text-sm text-[var(--fq-muted)]">
            No workouts found in the backend. Create one using the form.
          </section>
        }
      </section>

      <aside class="sticky top-6 space-y-4">
      <section class="glass-card h-fit p-5">
        <h2 class="mb-1 text-base font-semibold text-[var(--fq-text)]">
          {{ editingWorkoutId() ? 'Edit Workout' : 'Create Workout' }}
        </h2>
        <p class="mb-4 text-sm text-[var(--fq-muted)]">Saved to the workout service.</p>

        <form [formGroup]="createForm" (ngSubmit)="saveWorkoutForm()" class="space-y-3">
          <label class="field-label">
            Name
            <input class="field" formControlName="name" placeholder="Push Day" />
          </label>
          <label class="field-label">
            Difficulty
            <select class="field" formControlName="difficulty">
              <option value="Beginner">Beginner</option>
              <option value="Intermediate">Intermediate</option>
              <option value="Advanced">Advanced</option>
            </select>
          </label>
          <label class="field-label">
            Duration
            <input class="field" type="number" formControlName="estimatedDurationMinutes" min="1" />
          </label>
          <label class="field-label">
            Exercise
            <select class="field" formControlName="exerciseId">
              <option [ngValue]="0">Select exercise</option>
              @for (exercise of exercises(); track exercise.id) {
                <option [ngValue]="exercise.id">{{ exercise.name }} - {{ exercise.muscleGroup }}</option>
              }
            </select>
          </label>
          <button type="submit" class="btn-primary w-full">
            {{ editingWorkoutId() ? 'Update workout' : 'Create workout' }}
          </button>
          @if (editingWorkoutId()) {
            <button type="button" class="w-full rounded-md border border-[var(--fq-border)] px-4 py-2 text-sm font-medium text-[var(--fq-muted)]" (click)="resetCreateForm()">
              Cancel edit
            </button>
          }
        </form>
      </section>

      <section class="glass-card h-fit p-5">
        <h2 class="mb-1 text-base font-semibold text-[var(--fq-text)]">Log Workout</h2>
        <p class="mb-4 text-sm text-[var(--fq-muted)]">
          {{ selectedWorkout()?.name || 'Select a workout from the list' }}
        </p>
        <form [formGroup]="logForm" (ngSubmit)="saveLog()" class="space-y-3">
          <section class="grid grid-cols-3 gap-2">
            <label class="field-label">
              Sets
              <input class="field" type="number" formControlName="sets" min="1" />
            </label>
            <label class="field-label">
              Reps
              <input class="field" type="number" formControlName="reps" min="1" />
            </label>
            <label class="field-label">
              Weight
              <input class="field" type="number" formControlName="weightKg" min="0" />
            </label>
          </section>
          <label class="field-label">
            Duration
            <input class="field" type="number" formControlName="durationMinutes" min="1" />
          </label>
          <label class="field-label">
            Notes
            <textarea class="field min-h-24" formControlName="notes" placeholder="How did it feel?"></textarea>
          </label>
          <button type="submit" class="btn-primary w-full" [disabled]="!selectedWorkout()">Save workout log</button>
        </form>
      </section>
      </aside>
    </section>
  `,
  styles: `
    .field-label {
      display: block;
      color: var(--fq-muted);
      font-size: 0.75rem;
      font-weight: 600;
    }
    .field {
      margin-top: 0.25rem;
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
    .mini-btn {
      border: 1px solid var(--fq-border);
      border-radius: 0.375rem;
      padding: 0.35rem 0.6rem;
      color: var(--fq-accent);
      font-size: 0.75rem;
      font-weight: 600;
    }
    .mini-btn.danger {
      color: #b91c1c;
    }
  `,
})
export class WorkoutsComponent implements OnInit {
  private readonly workoutService = inject(WorkoutService);
  private readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  readonly workouts = signal<Workout[]>([]);
  readonly exercises = signal<Exercise[]>([]);
  readonly selectedWorkout = signal<Workout | null>(null);
  readonly editingWorkoutId = signal<number | null>(null);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);

  readonly createForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    difficulty: ['Beginner', Validators.required],
    estimatedDurationMinutes: [45, Validators.required],
    exerciseId: [0, Validators.required],
  });

  readonly logForm = this.fb.nonNullable.group({
    sets: [3, Validators.required],
    reps: [10, Validators.required],
    weightKg: [40],
    durationMinutes: [30, Validators.required],
    notes: [''],
  });

  ngOnInit(): void {
    this.loadExercises();
    this.loadWorkouts();
  }

  loadWorkouts(): void {
    this.workoutService.getAll().subscribe((list) => {
      const items = Array.isArray(list) ? list : [];
      this.workouts.set(items);
      if (items.length) {
        this.select(items[0]);
      } else {
        this.selectedWorkout.set(null);
      }
    });
  }

  groupedWorkouts(): WorkoutGroup[] {
    const groups = new Map<string, Workout[]>();
    for (const group of MUSCLE_GROUP_ORDER) {
      groups.set(group, []);
    }

    for (const workout of this.workouts()) {
      for (const group of this.muscleGroupsFor(workout)) {
        if (!groups.has(group)) {
          groups.set(group, []);
        }
        groups.get(group)?.push(workout);
      }
    }

    return Array.from(groups.entries())
      .filter(([, workouts]) => workouts.length > 0)
      .map(([name, workouts]) => ({ name, workouts }));
  }

  select(workout: Workout): void {
    this.selectedWorkout.set(workout);
    this.logForm.patchValue({
      durationMinutes: workout.durationMinutes || workout.estimatedDurationMinutes || 30,
    });
  }

  saveLog(): void {
    this.error.set(null);
    this.success.set(null);
    const workout = this.selectedWorkout();
    if (!workout || this.logForm.invalid) return;
    this.workoutService.logWorkout({
      workoutId: workout.id,
      ...this.logForm.getRawValue(),
    }).subscribe((result) => {
      if (!result) {
        this.error.set('Could not save workout log to backend.');
        return;
      }
      this.success.set('Workout log saved.');
      this.workoutService.notifyDataChanged();
    });
  }

  saveWorkoutForm(): void {
    this.error.set(null);
    this.success.set(null);
    const raw = this.createForm.getRawValue();
    if (this.createForm.invalid || !raw.exerciseId) {
      this.error.set('Select an exercise and complete the workout form.');
      return;
    }

    const payload = {
      name: raw.name,
      difficulty: raw.difficulty,
      estimatedDurationMinutes: raw.estimatedDurationMinutes,
      exerciseIds: [raw.exerciseId],
    };
    const editingId = this.editingWorkoutId();
    const request = editingId
      ? this.workoutService.updateWorkout(editingId, payload)
      : this.workoutService.createWorkout(payload);
    request.subscribe((workout) => {
      if (!workout) {
        this.error.set(editingId ? 'Could not update workout in backend.' : 'Could not create workout in backend.');
        return;
      }
      this.success.set(editingId ? 'Workout updated.' : 'Workout created.');
      this.resetCreateForm();
      this.loadWorkouts();
      this.workoutService.notifyDataChanged();
    });
  }

  editWorkout(workout: Workout): void {
    const firstExerciseId = workout.exercises?.[0]?.id ?? 0;
    this.editingWorkoutId.set(workout.id);
    this.createForm.reset({
      name: workout.name,
      difficulty: workout.difficulty || 'Beginner',
      estimatedDurationMinutes: workout.estimatedDurationMinutes || workout.durationMinutes || 45,
      exerciseId: firstExerciseId,
    });
  }

  deleteWorkout(workout: Workout): void {
    this.error.set(null);
    this.success.set(null);
    this.workoutService.deleteWorkout(workout.id).subscribe((deleted) => {
      if (!deleted) {
        this.error.set('Could not delete workout from backend.');
        return;
      }
      this.success.set('Workout deleted.');
      if (this.editingWorkoutId() === workout.id) {
        this.resetCreateForm();
      }
      this.loadWorkouts();
      this.workoutService.notifyDataChanged();
    });
  }

  resetCreateForm(): void {
    this.editingWorkoutId.set(null);
    this.createForm.reset({ name: '', difficulty: 'Beginner', estimatedDurationMinutes: 45, exerciseId: 0 });
  }

  exerciseNames(workout: Workout): string {
    return workout.exercises?.slice(0, 2).map((exercise) => exercise.name).join(', ') ?? '';
  }

  iconFor(group: string): string {
    const icons: Record<string, string> = {
      Chest: 'accessibility_new',
      Back: 'fitness_center',
      Shoulders: 'sports_gymnastics',
      Biceps: 'fitness_center',
      Triceps: 'fitness_center',
      Legs: 'directions_run',
      Glutes: 'directions_run',
      Abs: 'self_improvement',
      Cardio: 'favorite',
      'Full Body': 'exercise',
    };
    return icons[group] ?? 'fitness_center';
  }

  canManageWorkout(workout: Workout): boolean {
    return workout.userId === this.auth.currentUser()?.id;
  }

  private muscleGroupsFor(workout: Workout): string[] {
    const groups = new Set<string>();
    if (workout.muscleGroup) {
      groups.add(this.formatGroup(workout.muscleGroup));
    }
    workout.exercises?.forEach((exercise) => groups.add(this.formatGroup(exercise.muscleGroup)));
    if (groups.size === 0 && workout.type) {
      groups.add(this.formatGroup(workout.type));
    }
    if (groups.size === 0) {
      groups.add('Full Body');
    }
    return Array.from(groups);
  }

  private formatGroup(value: string): string {
    const normalized = value.replace(/_/g, ' ').toLowerCase();
    const known = MUSCLE_GROUP_ORDER.find((group) => group.toLowerCase() === normalized);
    if (known) return known;
    return normalized.replace(/\b\w/g, (char) => char.toUpperCase());
  }

  private loadExercises(): void {
    this.workoutService.getExercises().subscribe((list) => {
      this.exercises.set(Array.isArray(list) ? list : []);
      if (!Array.isArray(list) || list.length === 0) {
        this.error.set('Could not load exercises from backend.');
      }
    });
  }
}
