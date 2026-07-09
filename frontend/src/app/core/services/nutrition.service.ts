import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { catchError, map, of } from 'rxjs';
import { ApiBaseService } from './api-base.service';
import { CreateMealRequest, FoodItem, Meal } from '../models';

@Injectable({ providedIn: 'root' })
export class NutritionService extends ApiBaseService {
  private readonly dataChangedSubject = new Subject<void>();
  readonly dataChanged$ = this.dataChangedSubject.asObservable();

  notifyDataChanged(): void {
    this.dataChangedSubject.next();
  }

  getMeals(): Observable<Meal[]> {
    return this.get<Meal[]>('/nutrition/meals');
  }

  getFoods(): Observable<FoodItem[]> {
    return this.get<FoodItem[]>('/nutrition/foods');
  }

  logMeal(meal: CreateMealRequest): Observable<Meal> {
    return this.post<Meal>('/nutrition/meals', meal);
  }

  updateMeal(id: number, meal: CreateMealRequest): Observable<Meal> {
    return this.put<Meal>(`/nutrition/meals/${id}`, meal);
  }

  deleteMeal(id: number): Observable<boolean> {
    return this.http.delete<void>(`${this.baseUrl}/nutrition/meals/${id}`).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  getDailySummary(date?: string): Observable<{ calories: number; protein: number }> {
    const q = date ? `?date=${date}` : '';
    return this.get<{ calories: number; protein: number }>(`/nutrition/daily${q}`);
  }
}
