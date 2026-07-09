import { Injectable } from '@angular/core';
import { Observable, catchError, forkJoin, map, of } from 'rxjs';
import { ApiBaseService } from './api-base.service';
import { DashboardStats } from '../models';

interface WorkoutSessionDto {
  id: number;
  caloriesBurned?: number;
  loggedAt?: string;
}

interface ChallengeDto {
  id: string;
  status?: string;
}

interface UserXpDto {
  recentHistory?: XpHistoryDto[];
}

interface XpHistoryDto {
  amount: number;
  createdAt?: string;
}

interface NutritionReportDto {
  totalCalories?: number;
}

@Injectable({ providedIn: 'root' })
export class DashboardService extends ApiBaseService {
  getStats(): Observable<DashboardStats> {
    return forkJoin({
      workouts: this.http.get<WorkoutSessionDto[]>(`${this.baseUrl}/workouts/history`).pipe(catchError(() => of([]))),
      challenges: this.http.get<ChallengeDto[]>(`${this.baseUrl}/challenges?active=true`).pipe(catchError(() => of([]))),
      xp: this.http.get<UserXpDto>(`${this.baseUrl}/xp`).pipe(catchError(() => of({ recentHistory: [] }))),
      nutrition: this.http.get<NutritionReportDto>(`${this.baseUrl}/nutrition/report`).pipe(catchError(() => of({ totalCalories: 0 }))),
    }).pipe(
      map(({ workouts, challenges, xp, nutrition }) => {
        const sessions = Array.isArray(workouts) ? workouts : [];
        const activeChallenges = Array.isArray(challenges) ? challenges.length : 0;
        const xpHistory = Array.isArray(xp?.recentHistory) ? xp.recentHistory : [];

        return {
          totalWorkouts: sessions.length,
          weeklyCalories: this.weeklyCalories(sessions),
          nutritionCalories: nutrition?.totalCalories ?? 0,
          activeChallenges,
          currentStreak: this.currentStreak(sessions),
          xpThisWeek: this.xpThisWeek(xpHistory),
          workoutsByDay: this.workoutsByDay(sessions),
        };
      })
    );
  }

  private weeklyCalories(sessions: WorkoutSessionDto[]): number {
    const start = this.startOfToday();
    start.setDate(start.getDate() - 6);
    return Math.round(
      sessions
        .filter((session) => this.isOnOrAfter(session.loggedAt, start))
        .reduce((sum, session) => sum + (session.caloriesBurned ?? 0), 0)
    );
  }

  private currentStreak(sessions: WorkoutSessionDto[]): number {
    const workoutDays = new Set(
      sessions
        .map((session) => this.dateKey(session.loggedAt))
        .filter((value): value is string => Boolean(value))
    );

    let streak = 0;
    const cursor = this.startOfToday();
    while (workoutDays.has(this.dateKey(cursor.toISOString()) ?? '')) {
      streak += 1;
      cursor.setDate(cursor.getDate() - 1);
    }
    return streak;
  }

  private xpThisWeek(history: XpHistoryDto[]): number[] {
    const days = this.lastSevenDays();
    return days.map((day) =>
      history
        .filter((item) => this.dateKey(item.createdAt) === day.key)
        .reduce((sum, item) => sum + item.amount, 0)
    );
  }

  private workoutsByDay(sessions: WorkoutSessionDto[]) {
    const days = this.lastSevenDays();
    return days.map((day) => ({
      day: day.label,
      count: sessions.filter((session) => this.dateKey(session.loggedAt) === day.key).length,
    }));
  }

  private lastSevenDays() {
    const formatter = new Intl.DateTimeFormat('en-US', { weekday: 'short' });
    return Array.from({ length: 7 }, (_, index) => {
      const date = this.startOfToday();
      date.setDate(date.getDate() - (6 - index));
      return {
        key: this.dateKey(date.toISOString()) ?? '',
        label: formatter.format(date),
      };
    });
  }

  private isOnOrAfter(value: string | undefined, start: Date): boolean {
    if (!value) return false;
    return new Date(value).getTime() >= start.getTime();
  }

  private dateKey(value: string | undefined): string | null {
    if (!value) return null;
    return new Date(value).toISOString().slice(0, 10);
  }

  private startOfToday(): Date {
    const date = new Date();
    date.setHours(0, 0, 0, 0);
    return date;
  }
}
