import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { NutritionService } from '../../core/services/nutrition.service';
import { FoodItem, Meal } from '../../core/models';

@Component({
  selector: 'fq-nutrition',
  standalone: true,
  imports: [ReactiveFormsModule, MatIconModule],
  template: `
    <header class="mb-8">
      <h1 class="page-title">Nutrition</h1>
      <p class="text-sm text-[var(--fq-muted)]">Meals come from the backend. New meals are saved to the nutrition service.</p>
    </header>

    @if (error()) {
      <section class="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
        {{ error() }}
      </section>
    }
    @if (success()) {
      <section class="mb-4 rounded-lg border border-green-200 bg-green-50 px-4 py-3 text-sm font-medium text-green-700">
        {{ success() }}
      </section>
    }

    <section class="mb-6 grid gap-4 sm:grid-cols-3">
      <article class="glass-card p-5 text-center">
        <p class="text-sm text-[var(--fq-muted)]">Calories today</p>
        <p class="stat-value">{{ totals().calories }}</p>
      </article>
      <article class="glass-card p-5 text-center">
        <p class="text-sm text-[var(--fq-muted)]">Protein</p>
        <p class="stat-value">{{ totals().protein }}g</p>
      </article>
      <article class="glass-card p-5 text-center">
        <p class="text-sm text-[var(--fq-muted)]">Carbs / Fat</p>
        <p class="stat-value text-xl">{{ totals().carbs }}g / {{ totals().fat }}g</p>
      </article>
    </section>

    <section class="mb-6 grid gap-6 lg:grid-cols-[1fr_22rem]">
      <section class="space-y-3">
        @for (meal of meals(); track meal.id) {
          <article class="glass-card flex items-center justify-between gap-4 p-4">
            <section class="flex items-center gap-3">
              <mat-icon class="text-[var(--fq-accent)]">restaurant</mat-icon>
              <section>
                <h3 class="font-medium text-[var(--fq-text)]">{{ meal.name }}</h3>
                <p class="text-sm text-[var(--fq-muted)]">
                  P {{ protein(meal) }}g - C {{ carbs(meal) }}g - F {{ fat(meal) }}g
                </p>
              </section>
            </section>
            <section class="text-right">
              <span class="block font-semibold text-[var(--fq-accent)]">{{ calories(meal) }} kcal</span>
              <section class="mt-2 flex gap-2">
                <button type="button" class="mini-btn" (click)="editMeal(meal)">Edit</button>
                <button type="button" class="mini-btn danger" (click)="deleteMeal(meal)">Delete</button>
              </section>
            </section>
          </article>
        } @empty {
          <section class="glass-card p-6 text-sm text-[var(--fq-muted)]">
            No meals logged yet.
          </section>
        }
      </section>

      <aside class="glass-card h-fit p-5">
        <h2 class="mb-1 text-base font-semibold text-[var(--fq-text)]">
          {{ editingMealId() ? 'Edit Meal' : 'Log Meal' }}
        </h2>
        <p class="mb-4 text-sm text-[var(--fq-muted)]">Choose a real food item from the backend seed data.</p>

        <form [formGroup]="mealForm" (ngSubmit)="saveMeal()" class="space-y-3">
          <label class="field-label">
            Meal name
            <input class="field" formControlName="name" placeholder="Post-workout lunch" />
          </label>
          <label class="field-label">
            Type
            <select class="field" formControlName="mealType">
              <option value="BREAKFAST">Breakfast</option>
              <option value="LUNCH">Lunch</option>
              <option value="DINNER">Dinner</option>
              <option value="SNACK">Snack</option>
            </select>
          </label>
          <label class="field-label">
            Food
            <select class="field" formControlName="foodItemId">
              <option [ngValue]="0">Select food</option>
              @for (food of foods(); track food.id) {
                <option [ngValue]="food.id">{{ food.name }} - {{ food.category }}</option>
              }
            </select>
          </label>
          <label class="field-label">
            Quantity grams
            <input class="field" type="number" min="1" formControlName="quantityGrams" />
          </label>
          <button type="submit" class="btn-primary w-full">
            {{ editingMealId() ? 'Update meal' : 'Save meal' }}
          </button>
          @if (editingMealId()) {
            <button type="button" class="w-full rounded-md border border-[var(--fq-border)] px-4 py-2 text-sm font-medium text-[var(--fq-muted)]" (click)="resetMealForm()">
              Cancel edit
            </button>
          }
        </form>
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
export class NutritionComponent implements OnInit {
  private readonly nutrition = inject(NutritionService);
  private readonly fb = inject(FormBuilder);

  readonly meals = signal<Meal[]>([]);
  readonly foods = signal<FoodItem[]>([]);
  readonly editingMealId = signal<number | null>(null);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);

  readonly mealForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    mealType: ['LUNCH' as 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK'],
    foodItemId: [0, Validators.required],
    quantityGrams: [100, Validators.required],
  });

  ngOnInit(): void {
    this.loadMeals();
    this.nutrition.getFoods().subscribe((foods) => {
      this.foods.set(Array.isArray(foods) ? foods : []);
      if (!Array.isArray(foods) || foods.length === 0) {
        this.error.set('Could not load food list from backend.');
      }
    });
  }

  totals() {
    return this.meals().reduce(
      (total, meal) => ({
        calories: total.calories + this.calories(meal),
        protein: total.protein + this.protein(meal),
        carbs: total.carbs + this.carbs(meal),
        fat: total.fat + this.fat(meal),
      }),
      { calories: 0, protein: 0, carbs: 0, fat: 0 }
    );
  }

  saveMeal(): void {
    this.error.set(null);
    this.success.set(null);
    const raw = this.mealForm.getRawValue();
    if (this.mealForm.invalid || !raw.foodItemId) {
      this.error.set('Select a food and complete the meal form.');
      return;
    }

    const payload = {
      name: raw.name,
      mealType: raw.mealType,
      consumedAt: this.editingMealId()
        ? this.meals().find((meal) => meal.id === this.editingMealId())?.consumedAt ?? new Date().toISOString()
        : new Date().toISOString(),
      items: [{ foodItemId: raw.foodItemId, quantityGrams: raw.quantityGrams }],
    };
    const editingId = this.editingMealId();
    const request = editingId
      ? this.nutrition.updateMeal(editingId, payload)
      : this.nutrition.logMeal(payload);
    request.subscribe((meal) => {
      if (!meal) {
        this.error.set(editingId ? 'Could not update meal in backend.' : 'Could not save meal to backend.');
        return;
      }
      this.success.set(editingId ? 'Meal updated.' : 'Meal saved.');
      this.resetMealForm();
      this.loadMeals();
      this.nutrition.notifyDataChanged();
    });
  }

  editMeal(meal: Meal): void {
    const firstItem = meal.items?.[0];
    this.editingMealId.set(meal.id);
    this.mealForm.reset({
      name: meal.name,
      mealType: (meal.mealType as 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK') || 'LUNCH',
      foodItemId: firstItem?.foodItemId ?? 0,
      quantityGrams: firstItem?.quantityGrams ?? 100,
    });
  }

  deleteMeal(meal: Meal): void {
    this.error.set(null);
    this.success.set(null);
    this.nutrition.deleteMeal(meal.id).subscribe((deleted) => {
      if (!deleted) {
        this.error.set('Could not delete meal from backend.');
        return;
      }
      this.success.set('Meal deleted.');
      if (this.editingMealId() === meal.id) {
        this.resetMealForm();
      }
      this.loadMeals();
      this.nutrition.notifyDataChanged();
    });
  }

  resetMealForm(): void {
    this.editingMealId.set(null);
    this.mealForm.reset({ name: '', mealType: 'LUNCH', foodItemId: 0, quantityGrams: 100 });
  }

  calories(meal: Meal): number {
    return meal.totalCalories ?? meal.calories ?? 0;
  }

  protein(meal: Meal): number {
    return Math.round(meal.totalProteinG ?? meal.protein ?? 0);
  }

  carbs(meal: Meal): number {
    return Math.round(meal.totalCarbsG ?? meal.carbs ?? 0);
  }

  fat(meal: Meal): number {
    return Math.round(meal.totalFatG ?? meal.fat ?? 0);
  }

  private loadMeals(): void {
    this.nutrition.getMeals().subscribe((list) => {
      this.meals.set(Array.isArray(list) ? list : []);
    });
  }
}
