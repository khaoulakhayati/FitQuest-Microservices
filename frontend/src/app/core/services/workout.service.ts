import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { catchError, map, of } from 'rxjs';
import { ApiBaseService } from './api-base.service';
import { CreateWorkoutRequest, Exercise, Workout, WorkoutLogRequest } from '../models';

@Injectable({ providedIn: 'root' })
export class WorkoutService extends ApiBaseService {
  private readonly dataChangedSubject = new Subject<void>();
  readonly dataChanged$ = this.dataChangedSubject.asObservable();

  notifyDataChanged(): void {
    this.dataChangedSubject.next();
  }

  getAll(): Observable<Workout[]> {
    return this.get<Workout[]>('/workouts');
  }

  getById(id: number): Observable<Workout> {
    return this.get<Workout>(`/workouts/${id}`);
  }

  create(workout: Partial<Workout>): Observable<Workout> {
    return this.post<Workout>('/workouts', workout);
  }

  getExercises(): Observable<Exercise[]> {
    return this.get<Exercise[]>('/workouts/exercises');
  }

  createWorkout(workout: CreateWorkoutRequest): Observable<Workout> {
    return this.post<Workout>('/workouts', workout);
  }

  updateWorkout(id: number, workout: CreateWorkoutRequest): Observable<Workout> {
    return this.put<Workout>(`/workouts/${id}`, workout);
  }

  deleteWorkout(id: number): Observable<boolean> {
    return this.http.delete<void>(`${this.baseUrl}/workouts/${id}`).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  logWorkout(data: WorkoutLogRequest): Observable<unknown> {
    return this.post('/workouts/log', data);
  }
}
