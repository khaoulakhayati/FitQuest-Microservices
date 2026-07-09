import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiBaseService } from './api-base.service';
import { User } from '../models';

@Injectable({ providedIn: 'root' })
export class ProfileService extends ApiBaseService {
  getUsers(): Observable<User[]> {
    return this.get<User[]>('/users');
  }

  getProfile(): Observable<User> {
    return this.get<User>('/users/me');
  }

  updateProfile(data: Partial<User>): Observable<User> {
    return this.put<User>('/users/update', data);
  }
}
