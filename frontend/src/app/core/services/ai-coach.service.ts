import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiBaseService } from './api-base.service';
import { AiCoachMessage } from '../models';

@Injectable({ providedIn: 'root' })
export class AiCoachService extends ApiBaseService {
  getHistory(): Observable<AiCoachMessage[]> {
    return this.get<AiCoachMessage[]>('/ai/history');
  }

  sendMessage(content: string): Observable<AiCoachMessage> {
    return this.post<AiCoachMessage>('/ai/chat', { content });
  }
}
