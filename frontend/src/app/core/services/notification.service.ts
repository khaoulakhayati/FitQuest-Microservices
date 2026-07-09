import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiBaseService } from './api-base.service';
import { Notification } from '../models';

@Injectable({ providedIn: 'root' })
export class NotificationService extends ApiBaseService {
  getAll(): Observable<Notification[]> {
    return this.get<Notification[]>('/notifications');
  }

  markRead(id: number | string): Observable<Notification> {
    return this.patch<Notification>(`/notifications/${id}/read`, {});
  }

  markAllRead(): Observable<void> {
    return this.post<void>('/notifications/read-all', {});
  }
}
