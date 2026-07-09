import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiBaseService } from './api-base.service';
import { LeaderboardEntry, LeaderboardResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class LeaderboardService extends ApiBaseService {
  getGlobal(): Observable<LeaderboardEntry[]> {
    return this.get<LeaderboardResponse | LeaderboardEntry[]>('/leaderboard').pipe(
      map((response) => {
        if (Array.isArray(response)) {
          return response;
        }
        return response?.entries ?? [];
      })
    );
  }

  getWeekly(): Observable<LeaderboardEntry[]> {
    return this.get<LeaderboardEntry[]>('/leaderboard/weekly');
  }
}
