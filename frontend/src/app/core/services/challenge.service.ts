import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiBaseService } from './api-base.service';
import { Challenge } from '../models';

@Injectable({ providedIn: 'root' })
export class ChallengeService extends ApiBaseService {
  getAll(): Observable<Challenge[]> {
    return this.get<Challenge[]>('/challenges');
  }

  getById(id: string): Observable<Challenge> {
    return this.get<Challenge>(`/challenges/${id}`);
  }

  create(data: Partial<Challenge>): Observable<Challenge> {
    return this.post<Challenge>('/challenges', data);
  }

  update(id: string, data: Partial<Challenge>): Observable<Challenge> {
    return this.put<Challenge>(`/challenges/${id}`, data);
  }

  deleteChallenge(id: string): Observable<void> {
    return this.delete<void>(`/challenges/${id}`);
  }

  join(id: string, userId: number, displayName: string): Observable<unknown> {
    return this.post<unknown>(`/challenges/${id}/join`, { userId, displayName });
  }
}
